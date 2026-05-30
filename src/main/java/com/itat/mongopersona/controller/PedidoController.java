package com.itat.mongopersona.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.itat.mongopersona.event.Pedido;
import com.itat.mongopersona.service.PdfService;
import com.itat.mongopersona.service.PedidoService;

@Controller
public class PedidoController {

    @Autowired
    private PedidoService service;

    @Autowired
    private PdfService pdfService;

    // MOSTRAR PÁGINA
    @GetMapping("/pedidos")
    public String pedidos(Model model) {

        model.addAttribute("pedido",       new Pedido());
        model.addAttribute("lista",        service.listarPedidos());
        model.addAttribute("restantes",    10);
        model.addAttribute("totalPedidos", service.totalPedidosMes());
        model.addAttribute("totalPizzas",  service.totalPizzasVendidas());
        model.addAttribute("saborTop",     service.saborMasVendido());

        Map<String, Integer> reporte = service.pedidosPorSabor();
        model.addAttribute("sabores",    new ArrayList<>(reporte.keySet()));
        model.addAttribute("cantidades", new ArrayList<>(reporte.values()));

        return "pedidos";
    }

    // GUARDAR PEDIDO
    @PostMapping("/guardarPedido")
    public String guardarPedido(Pedido pedido, Model model) {

        try {
            service.guardarPedido(pedido);

        } catch (RuntimeException e) {

            model.addAttribute("error",        e.getMessage());
            model.addAttribute("lista",        service.listarPedidos());
            model.addAttribute("restantes",    service.pizzasRestantes(pedido.getHora()));
            model.addAttribute("totalPedidos", service.totalPedidosMes());
            model.addAttribute("totalPizzas",  service.totalPizzasVendidas());
            model.addAttribute("saborTop",     service.saborMasVendido());

            Map<String, Integer> reporte = service.pedidosPorSabor();
            model.addAttribute("sabores",    new ArrayList<>(reporte.keySet()));
            model.addAttribute("cantidades", new ArrayList<>(reporte.values()));

            return "pedidos";
        }

        return "redirect:/pedidos";
    }

    // ELIMINAR PEDIDO
    @GetMapping("/eliminarPedido/{id}")
    public String eliminarPedido(@PathVariable String id) {
        service.eliminarPedido(id);
        return "redirect:/pedidos";
    }

    // ✅ DESCARGAR PDF CON QR
    @GetMapping("/descargarComprobante/{id}")
    public ResponseEntity<byte[]> descargarComprobante(@PathVariable String id) {

        try {
            Pedido pedido = service.buscarPedido(id);
            byte[] pdf    = pdfService.generarComprobante(pedido);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"comprobante-" + id + ".pdf\"")
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ CONFIRMAR RECIBIDO (el repartidor escanea el QR)
    @GetMapping("/confirmarRecibido/{id}")
    public String confirmarRecibido(@PathVariable String id, Model model) {

        try {
            service.marcarRecibido(id);
            Pedido pedido = service.buscarPedido(id);

            // Precios por sabor
            java.util.Map<String, Integer> precios = new java.util.HashMap<>();
            precios.put("Hawaiana",    160);
            precios.put("Pepperoni",   150);
            precios.put("Mexicana",    170);
            precios.put("4 Quesos",    180);
            precios.put("Suprema",     190);
            precios.put("Vegetariana", 190);

            int precioPorPizza = precios.getOrDefault(pedido.getSabor(), 0);
            int total          = precioPorPizza * pedido.getCantidad();

            model.addAttribute("exito",   true);
            model.addAttribute("mensaje", "¡Pedido confirmado como entregado! ✅");
            model.addAttribute("pedido",  pedido);
            model.addAttribute("total",   total);

        } catch (Exception e) {
            model.addAttribute("exito",   false);
            model.addAttribute("mensaje", "No se pudo confirmar: " + e.getMessage());
        }

        return "confirmacion";
    }
}