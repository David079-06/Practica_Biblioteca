package com.example.practica_biblioteca.DTO;

public class VideojuegoListadoDTO {
    private VideojuegosDTO videojuego;
    private String desarrolladora;
    private int copiasTotales;
    private int copiasDisponibles;

    public VideojuegoListadoDTO(VideojuegosDTO videojuego, String desarrolladora,
                                int copiasTotales, int copiasDisponibles) {
        this.videojuego = videojuego;
        this.desarrolladora = desarrolladora;
        this.copiasTotales = copiasTotales;
        this.copiasDisponibles = copiasDisponibles;
    }

    public VideojuegosDTO getVideojuego() { return videojuego; }
    public String getDesarrolladora() { return desarrolladora; }
    public int getCopiasTotales() { return copiasTotales; }
    public int getCopiasDisponibles() { return copiasDisponibles; }
}
