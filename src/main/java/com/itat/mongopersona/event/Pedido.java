package com.itat.mongopersona.event;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pedidos")
public class Pedido {

    @Id
    private String id;

    private String sabor;
    private String direccion;
    private int cantidad;
    private String hora;
    private String correo;
    private String nombre;
    private String apellido;

    // ✅ NUEVO: indica si el pedido ya fue entregado
    private boolean recibido = false;

    public Pedido() {
    }

    public Pedido(String sabor, String direccion, int cantidad, String hora, String correo, String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.sabor = sabor;
        this.direccion = direccion;
        this.cantidad = cantidad;
        this.hora = hora;
        this.correo = correo;
        this.recibido = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSabor() {
        return sabor;
    }

    public void setSabor(String sabor) {
        this.sabor = sabor;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    // ✅ NUEVO
    public boolean isRecibido() {
        return recibido;
    }

    public void setRecibido(boolean recibido) {
        this.recibido = recibido;
    }
}