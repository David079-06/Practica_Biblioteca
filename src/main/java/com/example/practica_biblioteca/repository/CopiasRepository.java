package com.example.practica_biblioteca.repository;

import com.example.practica_biblioteca.DTO.CopiasDTO;
import com.example.practica_biblioteca.DTO.CopiaDisponibleDTO;
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

    public ArrayList<CopiaDisponibleDTO> buscarCopiasDisponibles() {
        String sql = "SELECT c.id, c.codigo_inventario, v.titulo, p.nombre AS plataforma "
                + "FROM copias c "
                + "JOIN videojuegos v ON c.id_videojuego = v.id "
                + "JOIN plataformas p ON c.id_plataforma = p.id "
                + "WHERE c.activa = 1 AND c.id NOT IN "
                + "(SELECT id_copia FROM prestamos WHERE fecha_devolucion IS NULL) "
                + "ORDER BY v.titulo, p.nombre, c.codigo_inventario";
        ArrayList<CopiaDisponibleDTO> listaCopias = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaCopias.add(new CopiaDisponibleDTO(
                        rs.getInt("id"),
                        rs.getString("codigo_inventario"),
                        rs.getString("titulo"),
                        rs.getString("plataforma")
                ));
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

    private CopiasDTO mapearCopia(ResultSet rs) throws SQLException {
        return new CopiasDTO(
                rs.getInt("id"),
                rs.getString("codigo_inventario"),
                rs.getInt("id_videojuego"),
                rs.getInt("id_plataforma"),
                rs.getString("estado"),
                rs.getBoolean("activa")
        );
    }
    public boolean estaDisponible(int idCopia) {
        String sql = "SELECT id FROM copias WHERE id = ? AND activa = 1 AND id NOT IN "
                + "(SELECT id_copia FROM prestamos WHERE fecha_devolucion IS NULL)";
        boolean disponible = false;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idCopia);
            ResultSet rs = ps.executeQuery();
            disponible = rs.next();
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al comprobar la disponibilidad de la copia", e);
            cerrarConexion(connection);
        }
        return disponible;
    }
    public CopiasDTO buscarCopiaPorId(int idCopia) {
        String sql = "SELECT id, codigo_inventario, id_videojuego, id_plataforma, estado, activa "
                + "FROM copias WHERE id = ?";
        CopiasDTO copia = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idCopia);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                copia = mapearCopia(rs);
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar copia", e);
            cerrarConexion(connection);
        }
        return copia;
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