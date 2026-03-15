package main.java.com.example.clinicafx;

import Repository.SQLPacientRepository;
import Repository.SQLProgramareRepository;
import Service.PacientService;
import Service.ProgramareService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SQLPacientRepository repoPacient = new SQLPacientRepository();
        SQLProgramareRepository repoProgramare = new SQLProgramareRepository(repoPacient);

        PacientService servicePacient = new PacientService(repoPacient);
        ProgramareService serviceProgramare = new ProgramareService(repoProgramare, repoPacient);

        if(servicePacient.getAll().isEmpty()){
            try {
                Utils.DataGenerator.generate(servicePacient, serviceProgramare, 100, 30);
                repoPacient.reload();
                repoProgramare.reload();
            } catch (Exception ignored){}
        } else {
            System.out.println("DB avea deja date, nu mai genereaza");
        }


        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 850);
        HelloController controller = fxmlLoader.getController();
        controller.setServices(servicePacient, serviceProgramare);

        stage.setTitle("Clinica Medicala");
        stage.setScene(scene);
        stage.show();
    }
}
