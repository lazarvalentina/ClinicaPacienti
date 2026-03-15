package main.java.Repository;

import Domain.Pacient;
import Domain.Programare;
import Exceptions.ObjectNotFoundException;
import Exceptions.RepositoryException;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Date;

public class SQLProgramareRepository extends Repository<Programare> implements AutoCloseable {

    private static final String JDBC_URL = "jdbc:sqlite:data/clinica.db";
    private Connection conn = null;
    private SQLPacientRepository pacientRepo;

    public SQLProgramareRepository(SQLPacientRepository pacientRepo) {
        this.pacientRepo = pacientRepo;
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
                CREATE TABLE IF NOT EXISTS programare(
                    id INTEGER PRIMARY KEY,
                    pacientId INTEGER NOT NULL,
                    data BIGINT NOT NULL,
                    scop TEXT NOT NULL,
                    FOREIGN KEY(pacientId) REFERENCES pacient(id)
                );
                """);
        } catch (SQLException e) {
            System.err.println("[ERROR] createSchema (programare): " + e.getMessage());
        }
    }

    private void loadData() {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM programare");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int pacientId = rs.getInt("pacientId");
                long timestamp = rs.getLong("data");
                String scop = rs.getString("scop");

                Pacient p = pacientRepo.getAll().stream()
                        .filter(x -> x.getId() == pacientId)
                        .findFirst()
                        .orElse(null);

                if (p == null) continue;

                Programare pr = new Programare(id, p, new Date(timestamp), scop);
                super.add(pr);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Programare p) throws RepositoryException {
        super.add(p);

        try (PreparedStatement stmt =
                     conn.prepareStatement("INSERT INTO programare VALUES (?, ?, ?, ?)")) {

            stmt.setInt(1, p.getId());
            stmt.setInt(2, p.getPacient().getId());
            stmt.setLong(3, p.getData().getTime());
            stmt.setString(4, p.getScop());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Eroare SQL la inserare programare", e);
        }
    }

    @Override
    public void update(Programare p) throws RepositoryException {
        try (PreparedStatement stmt =
                     conn.prepareStatement("UPDATE programare SET pacientId=?, data=?, scop=? WHERE id=?")) {

            stmt.setInt(1, p.getPacient().getId());
            stmt.setLong(2, p.getData().getTime());
            stmt.setString(3, p.getScop());
            stmt.setInt(4, p.getId());
            stmt.executeUpdate();

            super.update(p);

        } catch (SQLException e) {
            throw new RuntimeException("Eroare la modificarea programarii", e);
        }
    }

    @Override
    public void remove(int id) throws RepositoryException {
        try (PreparedStatement stmt =
                     conn.prepareStatement("DELETE FROM programare WHERE id=?")) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            super.remove(id);

        } catch (SQLException e) {
            throw new RuntimeException("Eroare la ștergere programare", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (conn != null) conn.close();
    }

    public void reload(){
        super.clear();
        loadData();
    }


}
