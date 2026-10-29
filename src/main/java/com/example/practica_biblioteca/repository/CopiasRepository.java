package com.example.practica_biblioteca.repository;

import com.example.practica_biblioteca.DTO.CopiasDTO;
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
public class CopiasRepository {
    private static final Logger logger = LoggerFactory.getLogger(CopiasRepository.class);
    private final DataSource dataSource;
    public CopiasRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<CopiasDTO> buscarCopias(String codigoInventario, String estado, Boolean activo) {
        String sql = "SELECT id, codigo_inventario, id_videojuego, id_plataforma , activo FROM copias";

        ArrayList<CopiasDTO> listaCopias = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + codigoInventario + "%");
            ps.setString(1, "%" + estado + "%");
            ps.setBoolean(4, activo);

            logger.info("Query a ejecutar: {}", ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaCopias.add(mapearCopias(rs));
            }
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar alumnos", e);
            cerrarConexion(connection);
        }
        return listaCopias;
    }

    private CopiasDTO mapearCopias(ResultSet rs) throws SQLException {
        return new CopiasDTO(
                rs.getInt("id"),
                rs.getString("codigo_inventario"),
                rs.getInt("id_videojuego"),
                rs.getInt("id_plataforma"),
                rs.getString("municipio"),
                rs.getBoolean("activo")
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