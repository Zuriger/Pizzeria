package com.itat.mongopersona.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.itat.mongopersona.model.Persona;
import com.itat.mongopersona.service.PersonaService;

/**
 * Controlador principal de reservaciones.
 */
@Controller
public class PController {

    @Autowired
    private PersonaService service;

    /**
     * Página principal.
     */
    @GetMapping("/")
    public String home() {

        return "home";
    }

    /**
     * Mostrar reservaciones.
     */
    @GetMapping("/personas")
    public String listar(Model model) {

        model.addAttribute(
                "persona",
                new Persona()
        );

        model.addAttribute(
                "personas",
                service.listarPersonas()
        );

        return "crud-persona";
    }

    /**
     * Guardar reservación.
     */
    @PostMapping("/guardar")
    public String guardar(
            Persona persona,
            Model model
    ) {

        try {

            service.guardarPersona(persona);

        } catch (RuntimeException e) {

            // MENSAJE DE ERROR
            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            // RECARGAR DATOS
            model.addAttribute(
                    "persona",
                    new Persona()
            );

            model.addAttribute(
                    "personas",
                    service.listarPersonas()
            );

            return "crud-persona";
        }

        return "redirect:/personas";
    }

    /**
     * Eliminar reservación.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable String id
    ) {

        service.eliminarPersona(id);

        return "redirect:/personas";
    }

    /**
     * Editar reservación.
     */
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable String id,
            Model model
    ) {

        Persona persona =
                service.obtenerPersonaPorId(id)
                        .orElse(null);

        if (persona == null) {

            return "redirect:/personas";
        }

        model.addAttribute(
                "persona",
                persona
        );

        return "edit-persona";
    }

    /**
     * Actualizar reservación.
     */
    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable String id,
            Persona persona,
            Model model
    ) {

        try {

            persona.setId(id);

            service.guardarPersona(persona);

        } catch (RuntimeException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute(
                    "persona",
                    persona
            );

            return "edit-persona";
        }

        return "redirect:/personas";
    }
}