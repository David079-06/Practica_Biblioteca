package com.example.practica_biblioteca.DTO;

public class CopiasDTO {
    private int id;
    private String codigoInventario;
    private int idVideojuego;
    private int idPlataforma;
    private String estado;
    private boolean activa;

    public CopiasDTO(int id, String codigoInventario, int idVideojuego, int idPlataforma, String estado, boolean activa) {
        this.id = id;
        this.codigoInventario = codigoInventario;
        this.idVideojuego = idVideojuego;
        this.idPlataforma = idPlataforma;
        this.estado = estado;
        this.activa = activa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoInventario() {
        return codigoInventario;
    }

    public void setCodigoInventario(String codigoInventario) {
        this.codigoInventario = codigoInventario;
    }

    public int getIdVideojuego() {
        return idVideojuego;
    }

    public void setIdVideojuego(int idVideojuego) {
        this.idVideojuego = idVideojuego;
    }

    public int getIdPlataforma() {
        return idPlataforma;
    }

    public void setIdPlataforma(int idPlataforma) {
        this.idPlataforma = idPlataforma;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
