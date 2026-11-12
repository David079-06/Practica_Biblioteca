package com.example.practica_biblioteca.repository;

import com.example.practica_biblioteca.DTO.DesarrolladorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class DesarrolladoresRepository {
    private static final Logger logger = LoggerFactory.getLogger(DesarrolladoresRepository.class);
    private final DataSource dataSource;

    public DesarrolladoresRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<DesarrolladorDTO> buscarDesarrolladoras() {
        String sql = "SELECT id, nombre, pais FROM desarrolladoras ORDER BY nombre";
        ArrayList<DesarrolladorDTO> listaDesarrolladoras = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaDesarrolladoras.add(new DesarrolladorDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("pais")
                ));
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar desarrolladoras", e);
            cerrarConexion(connection);
        }
        return listaDesarrolladoras;
    }

    private void cerrarConexion(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión", e);
            }
        }
    }
}