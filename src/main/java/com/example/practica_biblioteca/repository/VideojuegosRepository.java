package com.example.practica_biblioteca.repository;

import com.example.practica_biblioteca.DTO.VideojuegosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VideojuegosRepository {
    private static final Logger logger = LoggerFactory.getLogger(VideojuegosRepository.class);
    private final DataSource dataSource;

    public VideojuegosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<VideojuegosDTO> buscarVideojuegos(String titulo, int  anioLanzamiento , int pegi) {
        String sql = "SELECT v.id, v.titulo, v.anio_lanzamiento , v.pegi , v.id_desarrollador FROM videojuego v JOIN desarrollador d ON v.id_desarrollador = d.id";

        ArrayList<VideojuegosDTO> listaVideojuegos = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + titulo + "%");
            ps.setInt(2,  anioLanzamiento );
            ps.setInt(3,  pegi );

            logger.info("Query a ejecutar: {}", ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaVideojuegos.add(mapearVideojuegos(rs));
            }
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar alumnos", e);
            cerrarConexion(connection);
        }
        return listaVideojuegos;
    }

    private VideojuegosDTO mapearVideojuegos(ResultSet rs) throws SQLException {
        return new VideojuegosDTO(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getInt("anio_lanzamiento"),
                rs.getInt("pegi"),
                rs.getInt("id_desarrollador")
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
