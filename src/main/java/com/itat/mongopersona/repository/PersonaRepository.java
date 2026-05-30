package com.itat.mongopersona.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.itat.mongopersona.model.Persona;

/**
 * Interfaz repositorio para la entidad Persona.
 * 
 * Extiende MongoRepository para proporcionar
 * operaciones CRUD automáticas sobre MongoDB.
 * 
 * Permite realizar acciones como:
 * - Guardar registros
 * - Buscar por ID
 * - Obtener todos los clientes
 * - Eliminar registros
 * - Actualizar información
 * 
 * Spring Boot implementa esta interfaz automáticamente.
 *
 * @param <Persona> Entidad que será gestionada.
 * @param <String> Tipo de dato del identificador (ID).
 */
public interface PersonaRepository extends MongoRepository<Persona, String> {

}
