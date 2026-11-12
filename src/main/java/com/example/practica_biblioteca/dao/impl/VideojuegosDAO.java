package com.example.practica_biblioteca.dao.impl;

import com.example.practica_biblioteca.DTO.VideojuegosDTO;
import com.example.practica_biblioteca.DTO.VideojuegoListadoDTO;
import com.example.practica_biblioteca.dao.interfaz.IVideojuegosDAO;
import com.example.practica_biblioteca.repository.VideojuegosRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public class VideojuegosDAO implements IVideojuegosDAO {
    private final VideojuegosRepository videojuegosRepository;
    public VideojuegosDAO(VideojuegosRepository videojuegosRepository) {
        this.videojuegosRepository = videojuegosRepository;
    }
    @Override
    public ArrayList<VideojuegoListadoDTO> buscarVideojuegos(String titulo, Integer idDesarrolladora,
                                                              Integer anioDesde, Integer pegiMaximo,
                                                              boolean soloDisponibles) {
        return videojuegosRepository.buscarVideojuegos(titulo, idDesarrolladora,
                anioDesde, pegiMaximo, soloDisponibles);
    }
    @Override
    public VideojuegosDTO buscarVideojuegoPorId(int idVideojuego) {
        return videojuegosRepository.buscarVideojuegoPorId(idVideojuego);
    }
    @Override
    public int insertarVideojuego(VideojuegosDTO videojuego) {
        return videojuegosRepository.insertarVideojuego(videojuego);
    }
    @Override
    public int contarVideojuegos() {
        return videojuegosRepository.contarVideojuegos();
    }
}
