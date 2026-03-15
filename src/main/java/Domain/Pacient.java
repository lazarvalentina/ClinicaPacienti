package main.java.Domain;

public class Pacient extends Entity{

    private String nume;
    private String prenume;
    private int varsta;

    public Pacient(int id, String nume, String prenume, int varsta) throws Exception {
        super(id);

        if (nume == null || nume.isEmpty())
            throw new Exception("Numele nu poate fi gol!");
        if (prenume == null || prenume.isEmpty())
            throw new Exception("Prenumele nu poate fi gol!");
        if (varsta < 0)
            throw new Exception("Varsta nu poate fi negativa!");

        this.nume = nume;
        this.prenume = prenume;
        this.varsta = varsta;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public int getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public int getVarsta() {
        return varsta;
    }

    @Override
    public String toString() {
        return "Pacient{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", varsta=" + varsta +
                '}';
    }
}
