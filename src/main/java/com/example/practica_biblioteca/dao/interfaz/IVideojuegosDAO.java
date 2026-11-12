package com.example.practica_biblioteca.dao.interfaz;

import com.example.practica_biblioteca.DTO.VideojuegosDTO;
import com.example.practica_biblioteca.DTO.VideojuegoListadoDTO;
import java.util.ArrayList;

public interface IVideojuegosDAO {
    ArrayList<VideojuegoListadoDTO> buscarVideojuegos(String titulo, Integer idDesarrolladora,
                                                      Integer anioDesde, Integer pegiMaximo,
                                                      boolean soloDisponibles);
    VideojuegosDTO buscarVideojuegoPorId(int idVideojuego);
    int insertarVideojuego(VideojuegosDTO videojuego);
    int contarVideojuegos();
}
