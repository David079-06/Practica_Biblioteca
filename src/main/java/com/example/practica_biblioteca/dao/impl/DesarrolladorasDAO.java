package com.example.practica_biblioteca.dao.impl;

import com.example.practica_biblioteca.DTO.DesarrolladorDTO;
import com.example.practica_biblioteca.dao.interfaz.IDesarrolladorasDAO;
import com.example.practica_biblioteca.repository.DesarrolladorasRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public class DesarrolladorasDAO implements IDesarrolladorasDAO {
    private final DesarrolladorasRepository desarrolladorasRepository;
    public DesarrolladorasDAO(DesarrolladorasRepository desarrolladorasRepository) {
        this.desarrolladorasRepository = desarrolladorasRepository;
    }
    @Override
    public ArrayList<DesarrolladorDTO> buscarDesarrolladoras() {
        return desarrolladorasRepository.buscarDesarrolladoras();
    }
}
