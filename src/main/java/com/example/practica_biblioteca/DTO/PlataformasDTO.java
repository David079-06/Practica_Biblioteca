package com.example.practica_biblioteca.DTO;

public class PlataformasDTO {
    private int id;
    private String nombre;

    public PlataformasDTO(String nombre, int id) {
        this.nombre = nombre;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
