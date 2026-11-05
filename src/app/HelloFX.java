/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app; // mesmo pacote do Main

import javafx.application.Application; // classe base de todo app JavaFX
import javafx.scene.Scene; // o "cenário" que vai dentro da janela
import javafx.scene.control.Label; // componente de texto
import javafx.scene.layout.StackPane; // layout que empilha os componentes no centro
import javafx.stage.Stage; // a janela

public class HelloFX extends Application { // herda de Application

    @Override // sobrescreve o método que o JavaFX chama ao iniciar
    public void start(Stage stage) { // stage = a janela principal
        Label rotulo = new Label("JavaFX funcionando!"); // cria o texto
        StackPane raiz = new StackPane(rotulo); // coloca o texto dentro do layout
        Scene cena = new Scene(raiz, 400, 200); // cria o cenário com 400x200 pixels
        stage.setTitle("Teste JavaFX"); // título da janela
        stage.setScene(cena); // coloca o cenário na janela
        stage.show(); // mostra a janela
    }

    public static void main(String[] args) { // ponto de entrada normal do Java
        launch(args); // entrega o controle ao JavaFX (que chama o start)
    }
}
