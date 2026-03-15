module com.example.clinicafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens com.example.clinicafx to javafx.fxml;
    exports com.example.clinicafx;
}