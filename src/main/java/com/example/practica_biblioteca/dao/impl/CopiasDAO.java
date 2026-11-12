package com.example.practica_biblioteca.dao.impl;

import com.example.practica_biblioteca.DTO.CopiasDTO;
import com.example.practica_biblioteca.DTO.CopiaDisponibleDTO;
import com.example.practica_biblioteca.dao.interfaz.ICopiasDAO;
import com.example.practica_biblioteca.repository.CopiasRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public class CopiasDAO implements ICopiasDAO {
    private final CopiasRepository copiasRepository;
    public CopiasDAO(CopiasRepository copiasRepository) {
        this.copiasRepository = copiasRepository;
    }
    @Override
    public ArrayList<CopiaDisponibleDTO> buscarCopiasDisponibles() {
        return copiasRepository.buscarCopiasDisponibles();
    }
    @Override
    public CopiasDTO buscarCopiaPorId(int idCopia) {
        return copiasRepository.buscarCopiaPorId(idCopia);
    }
    @Override
    public boolean estaDisponible(int idCopia) {
        return copiasRepository.estaDisponible(idCopia);
    }
}
