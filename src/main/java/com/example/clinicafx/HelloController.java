package main.java.com.example.clinicafx;

import Domain.Pacient;
import Domain.Programare;
import Service.PacientService;
import Service.ProgramareService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HelloController {

    private PacientService servicePacient;
    private ProgramareService serviceProgramare;

    public void setServices(PacientService sp, ProgramareService sprog) {
        this.servicePacient = sp;
        this.serviceProgramare = sprog;
        loadPacienti();
        loadProgramari();
    }

    @FXML private TableView<Pacient> pacientTable;
    @FXML private TableView<Programare> programareTable;

    @FXML private TextField numeField, prenumeField, varstaField;
    @FXML private TextField dataField, scopField, pacientIdField;

    @FXML private ListView<String> listView;

    private final ObservableList<Pacient> pacientiModel = FXCollections.observableArrayList();
    private final ObservableList<Programare> programariModel = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        TableColumn<Pacient, Number> colIdPacient = new TableColumn<>("ID");
        colIdPacient.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));

        TableColumn<Pacient, String> colNume = new TableColumn<>("Nume");
        colNume.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNume()));

        TableColumn<Pacient, String> colPrenume = new TableColumn<>("Prenume");
        colPrenume.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenume()));

        TableColumn<Pacient, Number> colVarsta = new TableColumn<>("Vârsta");
        colVarsta.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getVarsta()));

        pacientTable.getColumns().setAll(colIdPacient, colNume, colPrenume, colVarsta);
        pacientTable.setItems(pacientiModel);

        TableColumn<Programare, Number> colIdPr = new TableColumn<>("ID");
        colIdPr.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));

        TableColumn<Programare, String> colPacient = new TableColumn<>("Pacient");
        colPacient.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getPacient().getNume() + " " + data.getValue().getPacient().getPrenume()
        ));

        TableColumn<Programare, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                new SimpleDateFormat("yyyy-MM-dd").format(data.getValue().getData())
        ));

        TableColumn<Programare, String> colScop = new TableColumn<>("Scop");
        colScop.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getScop()));

        programareTable.getColumns().setAll(colIdPr, colPacient, colData, colScop);
        programareTable.setItems(programariModel);
    }

    private void loadPacienti() {
        pacientiModel.clear();
        pacientiModel.addAll(servicePacient.getAll());
    }

    private void loadProgramari() {
        programariModel.clear();
        programariModel.addAll(serviceProgramare.getAll());
    }


    @FXML
    public void adaugaPacient() {
        try {
            String nume = numeField.getText();
            String prenume = prenumeField.getText();
            int varsta = Integer.parseInt(varstaField.getText());

            servicePacient.addPacient(nume, prenume, varsta);
            loadPacienti();
        } catch (Exception e) { showAlert(e.getMessage()); }
    }

    @FXML
    public void stergePacient() {
        Pacient p = pacientTable.getSelectionModel().getSelectedItem();
        if (p != null) {
            try {
                servicePacient.deletePacient(p.getId());
                loadPacienti();
            } catch (Exception e) { showAlert(e.getMessage()); }
        }
    }

    @FXML
    public void modificaPacient() {
        Pacient p = pacientTable.getSelectionModel().getSelectedItem();
        if (p != null) {
            try {
                servicePacient.updatePacient(p.getId(),
                        numeField.getText(),
                        prenumeField.getText(),
                        Integer.parseInt(varstaField.getText())
                );
                loadPacienti();
            } catch (Exception e) { showAlert(e.getMessage()); }
        }
    }


    @FXML
    public void adaugaProgramare() {
        try {
            int idp = Integer.parseInt(pacientIdField.getText());
            Date data = new SimpleDateFormat("yyyy-MM-dd").parse(dataField.getText());
            String scop = scopField.getText();

            serviceProgramare.addProgramare(idp, data, scop);
            loadProgramari();
        } catch (Exception e) { showAlert(e.getMessage()); }
    }

    @FXML
    public void raportProgPerPacient() {
        listView.getItems().setAll(
                serviceProgramare.getAll().stream()
                        .collect(Collectors.groupingBy(p -> p.getPacient().getId(), Collectors.counting()))
                        .entrySet().stream()
                        .sorted((a,b)->Long.compare(b.getValue(),a.getValue()))
                        .map(e -> "Pacient ID " + e.getKey() + " -> " + e.getValue() + " programări")
                        .toList()
        );
    }

    @FXML
    public void raportProgPeLuna() {
        SimpleDateFormat fmt = new SimpleDateFormat("MM-yyyy");
        listView.getItems().setAll(
                serviceProgramare.getAll().stream()
                        .collect(Collectors.groupingBy(p -> fmt.format(p.getData()), Collectors.counting()))
                        .entrySet().stream()
                        .sorted((a,b)->Long.compare(b.getValue(),a.getValue()))
                        .map(e -> e.getKey() + " -> " + e.getValue()+" programări")
                        .toList()
        );
    }

    @FXML
    public void raportZileDeLaUltima() {
        Date azi = new Date();
        listView.getItems().setAll(
                servicePacient.getAll().stream()
                        .map(p -> {
                            Programare u = serviceProgramare.getUltimaProgramare(p.getId());
                            long zile = (u != null) ? ((azi.getTime()-u.getData().getTime())/86400000) : 0;
                            return p.getNume()+" "+p.getPrenume()+" -> "+zile+" zile";
                        })
                        .sorted((a,b)->{
                            long x = Long.parseLong(a.replaceAll("\\D+",""));
                            long y = Long.parseLong(b.replaceAll("\\D+",""));
                            return Long.compare(y,x);
                        })
                        .toList()
        );
    }
    @FXML
    public void stergeProgramare() {
        Programare pr = programareTable.getSelectionModel().getSelectedItem();
        if (pr != null) {
            try {
                serviceProgramare.deleteProgramare(pr.getId());
                loadProgramari();
            } catch (Exception e) {
                showAlert(e.getMessage());
            }
        } else {
            showAlert("Selecteaza o programare înainte de stergere!");
        }
    }
    @FXML
    public void modificaProgramare() {
        Programare pr = programareTable.getSelectionModel().getSelectedItem();

        if (pr != null) {
            try {
                int pacientIdNou = Integer.parseInt(pacientIdField.getText());
                Date dataNoua = new SimpleDateFormat("yyyy-MM-dd").parse(dataField.getText());
                String scopNou = scopField.getText();

                serviceProgramare.updateProgramare(pr.getId(), pacientIdNou, dataNoua, scopNou);

                loadProgramari();

            } catch (Exception e) {
                showAlert(e.getMessage());
            }
        } else {
            showAlert("Selecteaza o programare inainte de modificare!");
        }
    }

    private void showAlert(String text) {
        new Alert(Alert.AlertType.ERROR,text).showAndWait();
    }
}
