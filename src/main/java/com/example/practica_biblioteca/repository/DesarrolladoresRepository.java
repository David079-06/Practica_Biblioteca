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

    public ArrayList<DesarrolladorDTO> buscarDesarroladores(String nombre, String pais) {
        String sql = "SELECT id, nombre, pais FROM desarrolador";

        ArrayList<DesarrolladorDTO> listaDesarrollador = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + nombre + "%");
            ps.setString(1, "%" + pais + "%");

            logger.info("Query a ejecutar: {}", ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaDesarrollador.add(mapearDesarrolladores(rs));
            }
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar alumnos", e);
            cerrarConexion(connection);
        }
        return listaDesarrollador;
    }

    private DesarrolladorDTO mapearDesarrolladores(ResultSet rs) throws SQLException {
        return new DesarrolladorDTO(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("pais")
        );
    }

    private void cerrarConexion(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}