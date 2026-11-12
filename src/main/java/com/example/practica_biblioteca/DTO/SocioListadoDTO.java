package com.example.practica_biblioteca.DTO;

public class SocioListadoDTO {
    private SociosDTO socio;
    private int edad;
    private int prestamosAbiertos;

    public SocioListadoDTO(SociosDTO socio, int edad, int prestamosAbiertos) {
        this.socio = socio;
        this.edad = edad;
        this.prestamosAbiertos = prestamosAbiertos;
    }

    public SociosDTO getSocio() { return socio; }
    public int getEdad() { return edad; }
    public int getPrestamosAbiertos() { return prestamosAbiertos; }
}
