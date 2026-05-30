package com.itat.mongopersona.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itat.mongopersona.event.Pedido;
import com.itat.mongopersona.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repo;

    @Autowired
    private ExecutorService virtualThreadExecutor;

    // BLOQUEO CONCURRENTE
    private final Object lock = new Object();

    /**
     * Guarda un pedido utilizando hilos virtuales.
     *
     * Solo permite máximo 10 pizzas por hora.
     *
     * @param pedido datos del pedido.
     * @return pedido guardado.
     */
    public Pedido guardarPedido(Pedido pedido) {

        synchronized (lock) {

            int pizzasVendidas =

                    repo.findAll()
                            .stream()
                            .filter(p ->

                                    p.getHora() != null
                                            &&

                                            p.getHora()
                                                    .substring(0, 2)
                                                    .equals(
                                                            pedido.getHora()
                                                                    .substring(0, 2)
                                                    )
                            )
                            .mapToInt(Pedido::getCantidad)
                            .sum();

            int disponibles =
                    10 - pizzasVendidas;

            if (pedido.getCantidad() > disponibles) {

                throw new RuntimeException(
                        "Ya no hay suficientes pizzas disponibles para esa hora"
                );
            }

            // HILO VIRTUAL
            virtualThreadExecutor.submit(() -> {

                System.out.println(
                        "Pedido procesado en hilo virtual: "
                                + Thread.currentThread()
                );

            });

            return repo.save(pedido);
        }
    }

    /**
     * Lista todos los pedidos.
     *
     * @return lista de pedidos.
     */
    public List<Pedido> listarPedidos() {

        return repo.findAll();
    }

    /**
     * Calcula pizzas restantes.
     *
     * @param hora hora del pedido.
     * @return pizzas disponibles.
     */
    public int pizzasRestantes(String hora) {

        int pizzasVendidas =

                repo.findAll()
                        .stream()
                        .filter(p ->

                                p.getHora() != null
                                        &&

                                        p.getHora()
                                                .substring(0, 2)
                                                .equals(
                                                        hora.substring(0, 2)
                                                )
                        )
                        .mapToInt(Pedido::getCantidad)
                        .sum();

        return 10 - pizzasVendidas;
    }

    /**
     * Elimina un pedido.
     *
     * @param id identificador.
     */
    public void eliminarPedido(String id) {

        synchronized (lock) {

            repo.deleteById(id);
        }
    }

    /**
     * TOTAL DE PEDIDOS.
     *
     * @return total pedidos.
     */
    public int totalPedidosMes() {

        return repo.findAll().size();
    }

    /**
     * TOTAL DE PIZZAS VENDIDAS.
     *
     * @return total pizzas.
     */
    public int totalPizzasVendidas() {

        return repo.findAll()
                .stream()
                .mapToInt(Pedido::getCantidad)
                .sum();
    }

    /**
     * SABOR MÁS VENDIDO.
     *
     * @return sabor top.
     */
    public String saborMasVendido() {

        return repo.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Pedido::getSabor,
                                Collectors.summingInt(
                                        Pedido::getCantidad
                                )
                        )
                )
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin datos");
    }

    /**
     * TOTAL DE PIZZAS VENDIDAS POR SABOR.
     *
     * @return mapa con sabor y total.
     */
    public Map<String, Integer> pedidosPorSabor() {

        return repo.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Pedido::getSabor,
                                Collectors.summingInt(
                                        Pedido::getCantidad
                                )
                        )
                );
    }

    // ✅ NUEVO: Busca un pedido por su id
    public Pedido buscarPedido(String id) {

        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));
    }

    // ✅ NUEVO: Marca el pedido como entregado cuando el repartidor escanea el QR
    public void marcarRecibido(String id) {

        synchronized (lock) {

            Pedido pedido = buscarPedido(id);
            pedido.setRecibido(true);
            repo.save(pedido);
        }
    }

}