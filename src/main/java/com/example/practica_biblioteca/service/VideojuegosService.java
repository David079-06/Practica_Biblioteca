package com.example.practica_biblioteca.service;

import com.example.practica_biblioteca.DTO.DesarrolladorDTO;
import com.example.practica_biblioteca.DTO.VideojuegosDTO;
import com.example.practica_biblioteca.DTO.VideojuegoListadoDTO;
import com.example.practica_biblioteca.dao.interfaz.IDesarrolladorasDAO;
import com.example.practica_biblioteca.dao.interfaz.IVideojuegosDAO;
import com.example.practica_biblioteca.excepcion.ReglaNegocioException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class VideojuegosService {
    private final IVideojuegosDAO videojuegosDAO;
    private final IDesarrolladorasDAO desarrolladorasDAO;

    public VideojuegosService(IVideojuegosDAO videojuegosDAO, IDesarrolladorasDAO desarrolladorasDAO) {
        this.videojuegosDAO = videojuegosDAO;
        this.desarrolladorasDAO = desarrolladorasDAO;
    }

    public ArrayList<VideojuegoListadoDTO> buscarVideojuegos(String titulo, Integer idDesarrolladora,
                                                              Integer anioDesde, Integer pegiMaximo,
                                                              boolean soloDisponibles) {
        return videojuegosDAO.buscarVideojuegos(titulo, idDesarrolladora,
                anioDesde, pegiMaximo, soloDisponibles);
    }

    public ArrayList<DesarrolladorDTO> buscarDesarrolladoras() {
        return desarrolladorasDAO.buscarDesarrolladoras();
    }

    public void insertarVideojuego(String titulo, String anioTexto, String pegiTexto,
                                   String idDesarrolladoraTexto) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new ReglaNegocioException("El título es obligatorio.");
        }
        if (anioTexto == null || anioTexto.trim().isEmpty()
                || pegiTexto == null || pegiTexto.trim().isEmpty()
                || idDesarrolladoraTexto == null || idDesarrolladoraTexto.trim().isEmpty()) {
            throw new ReglaNegocioException("Completa todos los campos.");
        }

        int anio;
        int pegi;
        int idDesarrolladora;
        try {
            anio = Integer.parseInt(anioTexto);
            pegi = Integer.parseInt(pegiTexto);
            idDesarrolladora = Integer.parseInt(idDesarrolladoraTexto);
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException("El año, PEGI y desarrolladora deben ser números.");
        }

        if (anio < 1970 || anio > LocalDate.now().getYear()) {
            throw new ReglaNegocioException("El año debe estar entre 1970 y el año actual.");
        }
        if (pegi != 3 && pegi != 7 && pegi != 12 && pegi != 16 && pegi != 18) {
            throw new ReglaNegocioException("El PEGI debe ser 3, 7, 12, 16 o 18.");
        }
        if (idDesarrolladora <= 0) {
            throw new ReglaNegocioException("Selecciona una desarrolladora.");
        }

        VideojuegosDTO videojuego = new VideojuegosDTO(0, titulo.trim(), anio, pegi, idDesarrolladora);
        if (videojuegosDAO.insertarVideojuego(videojuego) != 1) {
            throw new ReglaNegocioException("No se ha podido insertar el videojuego.");
        }
    }

    public int contarVideojuegos() {
        return videojuegosDAO.contarVideojuegos();
    }
}
