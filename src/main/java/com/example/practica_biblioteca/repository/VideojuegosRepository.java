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

    public ArrayList<VideojuegoListadoDTO> buscarVideojuegos(String titulo, Integer idDesarrolladora,
                                                              Integer anioDesde, Integer pegiMaximo,
                                                              boolean soloDisponibles) {
        String sql = "SELECT id, titulo, anio_lanzamiento, pegi, id_desarrolladora, desarrolladora, "
                + "copias_totales, copias_disponibles "
                + "FROM v_videojuegos_detalle WHERE 1 = 1";

        if (titulo != null && !titulo.trim().isEmpty()) {
            sql = sql + " AND titulo LIKE ?";
        }
        if (idDesarrolladora != null) {
            sql = sql + " AND id_desarrolladora = ?";
        }
        if (anioDesde != null) {
            sql = sql + " AND anio_lanzamiento >= ?";
        }
        if (pegiMaximo != null) {
            sql = sql + " AND pegi <= ?";
        }
        if (soloDisponibles) {
            sql = sql + " AND copias_disponibles > 0";
        }
        sql = sql + " ORDER BY titulo";

        ArrayList<VideojuegoListadoDTO> listaVideojuegos = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            int numeroParametro = 1;
            if (titulo != null && !titulo.trim().isEmpty()) {
                ps.setString(numeroParametro, "%" + titulo + "%");
                numeroParametro++;
            }
            if (idDesarrolladora != null) {
                ps.setInt(numeroParametro, idDesarrolladora);
                numeroParametro++;
            }
            if (anioDesde != null) {
                ps.setInt(numeroParametro, anioDesde);
                numeroParametro++;
            }
            if (pegiMaximo != null) {
                ps.setInt(numeroParametro, pegiMaximo);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaVideojuegos.add(new VideojuegoListadoDTO(
                        mapearVideojuego(rs),
                        rs.getString("desarrolladora"),
                        rs.getInt("copias_totales"),
                        rs.getInt("copias_disponibles")
                ));
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar videojuegos", e);
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

    public VideojuegosDTO buscarVideojuegoPorId(int idVideojuego) {
        String sql = "SELECT id, titulo, anio_lanzamiento, pegi, id_desarrolladora "
                + "FROM videojuegos WHERE id = ?";
        VideojuegosDTO videojuego = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idVideojuego);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                videojuego = mapearVideojuego(rs);
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar videojuego por ID", e);
            cerrarConexion(connection);
        }
        return videojuego;
    }


    public int contarVideojuegos() {
        String sql = "SELECT COUNT(*) AS total FROM videojuegos";
        int total = 0;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al contar videojuegos", e);
            cerrarConexion(connection);
        }
        return total;
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
    public int insertarVideojuego(VideojuegosDTO videojuego) {
        String sql = "INSERT INTO videojuegos (titulo, anio_lanzamiento, pegi, id_desarrolladora) "
                + "VALUES (?, ?, ?, ?)";
        int filasAfectadas = 0;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, videojuego.getTitulo());
            ps.setInt(2, videojuego.getAnioLanzamiento());
            ps.setInt(3, videojuego.getPegi());
            ps.setInt(4, videojuego.getIdDesarrolladora());
            filasAfectadas = ps.executeUpdate();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al insertar videojuego", e);
            cerrarConexion(connection);
        }
        return filasAfectadas;
    }
}
