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
        String sql = "SELECT c.id, c.codigo_inventario, c.id_videojuego, c.id_plataforma, "
                + "c.estado, c.activa, v.titulo, v.pegi, v.anio_lanzamiento, "
                + "p.nombre AS plataforma FROM copias c "
                + "JOIN videojuegos v ON c.id_videojuego = v.id "
                + "JOIN plataformas p ON c.id_plataforma = p.id "
                + "WHERE c.activa = 1 AND NOT EXISTS ("
                + "SELECT 1 FROM prestamos pr WHERE pr.id_copia = c.id "
                + "AND pr.fecha_devolucion IS NULL) "
                + "ORDER BY v.titulo, p.nombre, c.codigo_inventario";

        ArrayList<CopiasDTO> listaCopias = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            logger.info("Query a ejecutar: {}", ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaCopias.add(mapearCopias(rs));
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar copias disponibles", e);
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
                rs.getString("estado"),
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