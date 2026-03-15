package main.java.Service;



import Domain.Pacient;
import Repository.Repository;

import java.util.List;

public class PacientService {
    private Repository<Pacient> repo;

    public PacientService(Repository<Pacient> repo) {
        this.repo = repo;
    }

    public void addPacient(String nume, String prenume, int varsta) throws Exception {

        int id = repo.getAll().stream()
                .map(Pacient::getId)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        Pacient p = new Pacient(id, nume, prenume, varsta);
        repo.add(p);
    }

    public List<Pacient> getAll() {
        return repo.getAll();
    }

    public Pacient findById(int id) throws Exception {
        return repo.getAll().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new Exception("Pacientul cu ID " + id + " nu exista!"));
    }


    public void updatePacient(int id, String nume, String prenume, int varsta) throws Exception {

        Pacient original = findById(id);
        Pacient nou = new Pacient(id, nume, prenume, varsta);

        repo.update(nou);
    }

    public void deletePacient(int id) throws Exception {

        findById(id);
        repo.remove(id);
    }

    public List<Pacient> getAllPacienti() {
        return repo.getAll();
    }
}
