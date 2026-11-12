package com.example.practica_biblioteca.DTO;

public class PrestamoListadoDTO {
    private PrestamosDTO prestamo;
    private String socio;
    private String videojuego;
    private String plataforma;
    private String codigoInventario;
    private String estado;
    private int diasRetraso;

    public PrestamoListadoDTO(PrestamosDTO prestamo, String socio, String videojuego,
                              String plataforma, String codigoInventario) {
        this.prestamo = prestamo;
        this.socio = socio;
        this.videojuego = videojuego;
        this.plataforma = plataforma;
        this.codigoInventario = codigoInventario;
    }

    public PrestamosDTO getPrestamo() { return prestamo; }
    public String getSocio() { return socio; }
    public String getVideojuego() { return videojuego; }
    public String getPlataforma() { return plataforma; }
    public String getCodigoInventario() { return codigoInventario; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getDiasRetraso() { return diasRetraso; }
    public void setDiasRetraso(int diasRetraso) { this.diasRetraso = diasRetraso; }
}
