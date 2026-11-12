package com.example.practica_biblioteca.repository;
import com.example.practica_biblioteca.DTO.PlataformasDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlataformasRepository {
    private static final Logger logger = LoggerFactory.getLogger(PlataformasRepository.class);
    private final DataSource dataSource;

    public ArrayList<PlataformasDTO> buscarPlataformas() {
        String sql = "SELECT id, nombre FROM plataformas ORDER BY nombre";
        ArrayList<PlataformasDTO> listaPlataformas = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaPlataformas.add(new PlataformasDTO(
                        rs.getString("nombre"),
                        rs.getInt("id")
                ));
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar plataformas", e);
            cerrarConexion(connection);
        }
        return listaPlataformas;
    }

    private PlataformasDTO mapearPlataformas(ResultSet rs) throws SQLException {
        return new PlataformasDTO(
                rs.getString("nombre"),
                rs.getInt("id")

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
