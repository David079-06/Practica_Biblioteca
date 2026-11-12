package com.example.practica_biblioteca.DTO;

public class CopiaDisponibleDTO {
    private int id;
    private String codigoInventario;
    private String titulo;
    private String plataforma;

    public CopiaDisponibleDTO(int id, String codigoInventario, String titulo, String plataforma) {
        this.id = id;
        this.codigoInventario = codigoInventario;
        this.titulo = titulo;
        this.plataforma = plataforma;
    }

    public int getId() { return id; }
    public String getCodigoInventario() { return codigoInventario; }
    public String getTitulo() { return titulo; }
    public String getPlataforma() { return plataforma; }

    public String getDescripcion() {
        return titulo + " (" + plataforma + ") - " + codigoInventario;
    }
}
