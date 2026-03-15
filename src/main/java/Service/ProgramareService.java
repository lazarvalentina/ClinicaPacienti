package main.java.Service;

import Domain.Pacient;
import Domain.Programare;
import Repository.Repository;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ProgramareService {

    private Repository<Programare> repoProgramari;
    private Repository<Pacient> repoPacienti;

    public ProgramareService(Repository<Programare> repoProgramari, Repository<Pacient> repoPacienti) {
        this.repoProgramari = repoProgramari;
        this.repoPacienti = repoPacienti;
    }

    public void addProgramare(int pacientId, Date data, String scop) throws Exception {

        Pacient pacient = repoPacienti.getAll().stream()
                .filter(p -> p.getId() == pacientId)
                .findFirst()
                .orElseThrow(() -> new Exception("Pacientul nu există!"));

        int newId = repoProgramari.getAll().stream()
                .map(Programare::getId)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        for (Programare p : repoProgramari.getAll()) {
            long dif = Math.abs(p.getData().getTime() - data.getTime());
            if (dif < 3600000) {
                throw new Exception("Există deja o programare în acea oră!");
            }
        }

        Programare pr = new Programare(newId, pacient, data, scop);
        repoProgramari.add(pr);
    }


    public List<Programare> getAll() {
        return repoProgramari.getAll();
    }

    public void updateProgramare(int id, int pacientId, Date data, String scop) throws Exception {

        Pacient pacient = repoPacienti.getAll().stream()
                .filter(p -> p.getId() == pacientId)
                .findFirst()
                .orElseThrow(() -> new Exception("Pacientul nu există!"));

        Programare updated = new Programare(id, pacient, data, scop);
        repoProgramari.update(updated);
    }


    public void deleteProgramare(int id) throws Exception {
        repoProgramari.remove(id);
    }

    public Programare getUltimaProgramare(int idPacient) {
        return repoProgramari.getAll().stream()
                .filter(p -> p.getPacient().getId() == idPacient)
                .max(Comparator.comparing(Programare::getData))
                .orElse(null);
    }
}
