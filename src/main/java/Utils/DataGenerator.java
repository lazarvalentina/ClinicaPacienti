package main.java.Utils;

import Service.PacientService;
import Service.ProgramareService;

import java.util.*;

public class DataGenerator {

    private static final String[] NUME = {
            "Popescu", "Ionescu", "Georgescu", "Lazar", "Dumitrescu", "Enache",
            "Matei", "Radulescu", "Ciobanu", "Voicu", "Dobre", "Stefan", "Florea",
            "Moldovan", "Sandu", "Stoica", "Avram", "Neagu", "Cristea", "Tudor"
    };

    private static final String[] PRENUME = {
            "Andrei", "Bianca", "Ioana", "Mihai", "Elena", "Ana", "Vlad", "Paul",
            "Alexandru", "Maria", "Cristian", "Andreea", "Rares", "Teodora",
            "Daria", "Stefan", "Denisa", "Adrian", "Roxana", "Florin"
    };

    private static final String[] SCOPURI = {
            "Consultatie generala",
            "Control analize",
            "Monitorizare tratament",
            "Rezultat investigatii",
            "Consultatie cardiologie",
            "Control dermatologie",
            "Interpretare radiografie",
            "Control post-operator",
            "Consultatie ORL",
            "Verificare reteta medicala"
    };

    public static void generate(PacientService pacientService,
                                ProgramareService programareService,
                                int nrPacienti, int nrProgramari) throws Exception {

        Random random = new Random();

        for (int i=0; i<nrPacienti; i++){
            String nume = NUME[random.nextInt(NUME.length)];
            String prenume = PRENUME[random.nextInt(PRENUME.length)];
            int varsta = 18 + random.nextInt(65);

            pacientService.addPacient(nume, prenume, varsta);
        }

        List<Integer> pacientiExistenti = pacientService.getAll().stream()
                .map(p -> p.getId()).toList();

        int generateCount = 0;
        int incercari = 0;
        int maxIncercari = nrProgramari * 10;

        while(generateCount < nrProgramari && incercari < maxIncercari){
            incercari++;

            int pacientId = pacientiExistenti.get(random.nextInt(pacientiExistenti.size()));

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -random.nextInt(180));

            int ora = 8 + random.nextInt(10);
            int minute = random.nextBoolean() ? 0 : 30;
            cal.set(Calendar.HOUR_OF_DAY, ora);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);

            Date data = cal.getTime();
            String scop = SCOPURI[random.nextInt(SCOPURI.length)];

            try {
                programareService.addProgramare(pacientId, data, scop);
                generateCount++;
            }catch(Exception ignored){}
        }

    }
}
