package app; // mesmo pacote do Main, do HelloFX e do AgendaFX

import database.ConexaoBD; // para criar a tabela ao iniciar
import java.io.IOException; // erro ao carregar o FXML
import java.sql.SQLException; // erro ao criar a tabela
import javafx.application.Application; // classe base de todo app JavaFX
import javafx.application.Platform; // permite encerrar o JavaFX por código
import javafx.fxml.FXMLLoader; // lê o arquivo FXML e monta a tela
import javafx.scene.Parent; // tipo da raiz da tela montada
import javafx.scene.Scene; // o cenário da janela
import javafx.scene.control.Alert; // janelas de aviso
import javafx.stage.Stage; // a janela

public class AgendaApp extends Application { // só abre a janela: a tela está no FXML e a lógica no Controller

    @Override // sobrescreve o método que o JavaFX chama ao iniciar
    public void start(Stage stage) throws IOException { // pode lançar IOException se o FXML não for encontrado
        try { // protege a criação da tabela
            ConexaoBD.criarTabelas(); // cria a tabela contatos se ainda não existir
        } catch (SQLException e) { // banco indisponível
            new Alert(Alert.AlertType.ERROR, "Não foi possível preparar o banco: " + e.getMessage()).showAndWait(); // avisa
            Platform.exit(); // encerra o JavaFX (não há janela para mostrar)
            return; // sai do método
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AgendaView.fxml")); // procura o FXML em src/main/resources/ui
        Parent raiz = loader.load(); // monta a tela e cria o AgendaController automaticamente

        stage.setTitle("Agenda de Contatos"); // título da janela
        stage.setScene(new Scene(raiz)); // o tamanho vem do prefWidth/prefHeight do FXML
        stage.show(); // mostra a janela
    }

    public static void main(String[] args) { // ponto de entrada normal do Java
        launch(args); // entrega o controle ao JavaFX (que chama o start)
    }
}