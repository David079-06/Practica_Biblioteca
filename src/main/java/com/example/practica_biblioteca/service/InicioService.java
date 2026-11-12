package com.example.practica_biblioteca.service;

import com.example.practica_biblioteca.dao.interfaz.IVideojuegosDAO;
import com.example.practica_biblioteca.dao.interfaz.ISociosDAO;
import com.example.practica_biblioteca.dao.interfaz.IPrestamosDAO;
import org.springframework.stereotype.Service;

@Service
public class InicioService {
    private final IVideojuegosDAO videojuegosDAO;
    private final ISociosDAO sociosDAO;
    private final IPrestamosDAO prestamosDAO;

    public InicioService(IVideojuegosDAO videojuegosDAO, ISociosDAO sociosDAO,
                         IPrestamosDAO prestamosDAO) {
        this.videojuegosDAO = videojuegosDAO;
        this.sociosDAO = sociosDAO;
        this.prestamosDAO = prestamosDAO;
    }

    public int contarVideojuegos() { return videojuegosDAO.contarVideojuegos(); }
    public int contarSociosActivos() { return sociosDAO.contarSociosActivos(); }
    public int contarPrestamosAbiertos() { return prestamosDAO.contarPrestamosAbiertos(); }
    public int contarPrestamosVencidos() { return prestamosDAO.contarPrestamosVencidos(); }
}
