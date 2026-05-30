package com.itat.mongopersona.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itat.mongopersona.model.Persona;
import com.itat.mongopersona.repository.PersonaRepository;

/**
 * Servicio de reservaciones.
 */
@Service
public class PersonaService {

    @Autowired
    private PersonaRepository repo;

    // HILOS VIRTUALES
    @Autowired
    private ExecutorService virtualThreadExecutor;

    // BLOQUEO PARA EVITAR RESERVAS DUPLICADAS
    private final Object lock = new Object();

    /**
     * Lista todas las reservaciones.
     */
    public List<Persona> listarPersonas() {

        return repo.findAll();
    }

    /**
     * Guarda una reservación.
     * 
     * Valida que no exista la misma mesa
     * en la misma fecha y hora.
     */
    public Persona guardarPersona(Persona persona) {

        synchronized (lock) {

            // VALIDAR SI LA MESA YA ESTÁ OCUPADA
            boolean ocupada = repo.findAll()
                    .stream()
                    .anyMatch(p ->

                            p.getNoMesa().equals(
                                    persona.getNoMesa()
                            )

                            &&

                            p.getFecha().equals(
                                    persona.getFecha()
                            )

                            &&

                            p.getHora().equals(
                                    persona.getHora()
                            )
                    );

            if (ocupada) {

                throw new RuntimeException(
                        "La mesa ya está reservada en esa fecha y hora"
                );
            }

            // HILO VIRTUAL
            virtualThreadExecutor.submit(() -> {

                System.out.println(
                        "Reservación procesada en hilo virtual: "
                                + Thread.currentThread()
                );

            });

            return repo.save(persona);
        }
    }

    /**
     * Elimina reservación.
     */
    public void eliminarPersona(String id) {

        synchronized (lock) {

            repo.deleteById(id);
        }
    }

    /**
     * Buscar por ID.
     */
    public Optional<Persona> obtenerPersonaPorId(String id) {

        return repo.findById(id);
    }
}