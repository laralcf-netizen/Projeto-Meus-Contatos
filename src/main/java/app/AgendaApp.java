package app; 

import database.ConexaoBD; 
import java.io.IOException; 
import java.sql.SQLException; 
import javafx.application.Application; 
import javafx.application.Platform; 
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; 
import javafx.scene.Scene; 
import javafx.scene.control.Alert; 
import javafx.stage.Stage; 

public class AgendaApp extends Application { 

    @Override 
    public void start(Stage stage) throws IOException { 
        try { 
            ConexaoBD.criarTabelas(); 
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Não foi possível preparar o banco: " + e.getMessage()).showAndWait(); // avisa
            Platform.exit(); 
            return; 
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AgendaView.fxml")); 
        Parent raiz = loader.load(); 

        stage.setTitle("Agenda de Contatos"); 
        stage.setScene(new Scene(raiz)); 
        stage.show(); 
    }

    public static void main(String[] args) { 
        launch(args); 
    }
}