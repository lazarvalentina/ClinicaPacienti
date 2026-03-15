package main.java.Repository;

import Domain.Pacient;
import Exceptions.ObjectNotFoundException;
import Exceptions.RepositoryException;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLPacientRepository extends Repository<Pacient> implements AutoCloseable {

    private static final String JDBC_URL = "jdbc:sqlite:data/clinica.db";
    private Connection conn = null;

    public SQLPacientRepository() {
        openConnection();
        createSchema();
        loadData();
    }

    private void openConnection() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(JDBC_URL);
            if (conn == null || conn.isClosed())
                conn = ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la conectarea cu baza de date", e);
        }
    }

    private void createSchema() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS pacient(
                    id INTEGER PRIMARY KEY,
                    nume TEXT NOT NULL,
                    prenume TEXT NOT NULL,
                    varsta INTEGER NOT NULL
                );
                """);
        } catch (SQLException e) {
            System.err.println("[ERROR] createSchema: " + e.getMessage());
        }
    }

    private void loadData() {
        try (PreparedStatement st = conn.prepareStatement("SELECT * FROM pacient");
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Pacient p = new Pacient(
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getString("prenume"),
                        rs.getInt("varsta")
                );
                super.add(p);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Pacient p) throws RepositoryException {
        super.add(p);

        try (PreparedStatement st = conn.prepareStatement(
                "INSERT INTO pacient(id, nume, prenume, varsta) VALUES ( ?, ?, ?, ?)")) {

            st.setInt(1, p.getId());
            st.setString(2, p.getNume());
            st.setString(3, p.getPrenume());
            st.setInt(4, p.getVarsta());
            st.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Eroare SQL la inserare pacient", e);
        }
    }

    @Override
    public void update(Pacient p) throws RepositoryException {
        try (PreparedStatement st = conn.prepareStatement(
                "UPDATE pacient SET nume=?, prenume=?, varsta=? WHERE id=?")) {

            st.setString(1, p.getNume());
            st.setString(2, p.getPrenume());
            st.setInt(3, p.getVarsta());
            st.setInt(4, p.getId());
            st.executeUpdate();

            super.update(p);

        } catch (SQLException e) {
            throw new RuntimeException("Eroare SQL la update pacient", e);
        }
    }

    @Override
    public void remove(int id) throws RepositoryException {
        try (PreparedStatement st = conn.prepareStatement("DELETE FROM pacient WHERE id = ?")) {

            st.setInt(1, id);
            st.executeUpdate();
            super.remove(id);

        } catch (SQLException e) {
            throw new RuntimeException("Eroare SQL la ștergere pacient", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (conn != null)
            conn.close();
    }
    public void reload(){
        super.clear();
        loadData();
    }



}
