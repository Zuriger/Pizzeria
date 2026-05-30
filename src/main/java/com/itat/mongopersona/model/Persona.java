package com.itat.mongopersona.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clase Persona que representa a un cliente con reservación
 * dentro del sistema de la pizzería.
 * 
 * Se almacena en la colección "persona" de MongoDB.
 */
@Document(collection = "persona")
public class Persona {

    @Id
    private String id;

    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private Integer noMesa;
    private String hora;
    private Integer numeroPersonas;
    private String fecha;

    /**
     * Constructor vacío requerido por Spring y MongoDB.
     */
    public Persona() {}

    /**
     * Constructor con parámetros para inicializar una reservación.
     * 
     * @param nombre Nombre del cliente.
     * @param apellido Apellido del cliente.
     * @param telefono Número telefónico del cliente.
     * @param correo Correo electrónico del cliente.
     * @param noMesa Número de mesa reservada.
     * @param hora Hora de la reservación.
     * @param numeroPersonas Cantidad de personas.
     * @param fecha Fecha de la reservación.
     */
    public Persona(String nombre, String apellido, String telefono, String correo,
                   Integer noMesa, String hora, Integer numeroPersonas, String fecha) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.noMesa = noMesa;
        this.hora = hora;
        this.numeroPersonas = numeroPersonas;
        this.fecha = fecha;
    }

    /**
     * Obtiene el ID del registro.
     * 
     * @return ID único del cliente.
     */
    public String getId() {
        return id;
    }

    /**
     * Asigna el ID del registro.
     * 
     * @param id Identificador único.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del cliente.
     * 
     * @return Nombre del cliente.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre del cliente.
     * 
     * @param nombre Nombre del cliente.
     * @exception IllegalArgumentException Si el nombre es nulo.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del cliente.
     * 
     * @return Apellido del cliente.
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Asigna el apellido del cliente.
     * 
     * @param apellido Apellido del cliente.
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el teléfono del cliente.
     * 
     * @return Número telefónico.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Asigna el teléfono del cliente.
     * 
     * @param telefono Número telefónico.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el correo electrónico.
     * 
     * @return Correo del cliente.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Asigna el correo electrónico.
     * 
     * @param correo Correo del cliente.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene el número de mesa reservada.
     * 
     * @return Número de mesa.
     */
    public Integer getNoMesa() {
        return noMesa;
    }

    /**
     * Asigna el número de mesa.
     * 
     * @param noMesa Número de mesa reservada.
     */
    public void setNoMesa(Integer noMesa) {
        this.noMesa = noMesa;
    }

    /**
     * Obtiene la hora de reservación.
     * 
     * @return Hora reservada.
     */
    public String getHora() {
        return hora;
    }

    /**
     * Asigna la hora de reservación.
     * 
     * @param hora Hora deseada.
     */
    public void setHora(String hora) {
        this.hora = hora;
    }

    /**
     * Obtiene la cantidad de personas.
     * 
     * @return Número de personas.
     */
    public Integer getNumeroPersonas() {
        return numeroPersonas;
    }

    /**
     * Asigna la cantidad de personas.
     * 
     * @param numeroPersonas Número de asistentes.
     */
    public void setNumeroPersonas(Integer numeroPersonas) {
        this.numeroPersonas = numeroPersonas;
    }

    /**
     * Obtiene la fecha de la reservación.
     * 
     * @return Fecha reservada.
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Asigna la fecha de reservación.
     * 
     * @param fecha Fecha deseada.
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
