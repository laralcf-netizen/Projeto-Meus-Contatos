package app; // mesmo pacote do Main e do HelloFX

import controller.Agenda; // a agenda que já usamos no console
import database.ConexaoBD; // para criar a tabela ao iniciar
import exceptions.RepositorioException; // erro traduzido do banco (não verificada)
import java.sql.SQLException; // erro ao criar a tabela
import javafx.application.Application; // classe base de todo app JavaFX
import javafx.application.Platform; // permite encerrar o JavaFX por código
import javafx.beans.property.SimpleStringProperty; // valor de texto "observável" para as células da tabela
import javafx.collections.FXCollections; // fábrica de listas observáveis
import javafx.collections.ObservableList; // lista que avisa a tabela quando muda
import javafx.geometry.Insets; // margens internas
import javafx.scene.Scene; // o cenário da janela
import javafx.scene.control.Alert; // janelas de aviso
import javafx.scene.control.Button; // botão
import javafx.scene.control.ButtonType; // botões padrão das janelas de aviso (Sim/Não)
import javafx.scene.control.CheckBox; // caixa de seleção
import javafx.scene.control.Label; // texto fixo
import javafx.scene.control.TableColumn; // coluna da tabela
import javafx.scene.control.TableView; // a tabela
import javafx.scene.control.TextField; // campo de texto
import javafx.scene.layout.BorderPane; // layout com topo, centro e rodapé
import javafx.scene.layout.GridPane; // layout em grade (linhas e colunas)
import javafx.scene.layout.HBox; // layout horizontal
import javafx.scene.layout.VBox; // layout vertical
import javafx.stage.Stage; // a janela
import model.Contato; // contato comum
import model.ContatoComercial; // contato com empresa
import repository.RepositorioContatosJDBC; // repositório ligado ao MySQL
import utils.ValidadorEmail; // validação de e-mail que já existe

public class AgendaFX extends Application { // tela principal da agenda

    private final Agenda<Contato> agenda = new Agenda<>(new RepositorioContatosJDBC()); // a mesma Agenda do console, agora com tela
    private final ObservableList<Contato> dados = FXCollections.observableArrayList(); // lista observável: a tabela se atualiza sozinha quando ela muda
    private final TableView<Contato> tabela = new TableView<>(); // tabela que mostra objetos Contato (generics na prática)

    private final TextField campoNome = new TextField(); // campo do nome
    private final TextField campoTelefone = new TextField(); // campo do telefone
    private final TextField campoEmail = new TextField(); // campo do e-mail
    private final CheckBox checkComercial = new CheckBox("Contato comercial"); // marca se o contato é comercial
    private final TextField campoEmpresa = new TextField(); // campo da empresa (só vale se for comercial)
    private final TextField campoBusca = new TextField(); // campo da busca por nome
    private final Label status = new Label("Pronto."); // mensagem de status no rodapé

    @Override // sobrescreve o método que o JavaFX chama ao iniciar
    public void start(Stage stage) { // stage = a janela principal
        try { // protege a criação da tabela
            ConexaoBD.criarTabelas(); // cria a tabela contatos se ainda não existir
        } catch (SQLException e) { // banco indisponível
            mostrarErro("Não foi possível preparar o banco: " + e.getMessage()); // avisa o usuário
            Platform.exit(); // encerra o JavaFX (não há janela para mostrar)
            return; // sai do método
        }

        montarTabela(); // configura as colunas da tabela

        BorderPane raiz = new BorderPane(); // layout principal: topo, centro e rodapé
        raiz.setTop(montarFormulario()); // formulário no topo
        raiz.setCenter(montarAreaDaTabela()); // busca + tabela no centro
        BorderPane.setMargin(status, new Insets(8, 12, 8, 12)); // margem em volta da mensagem de status
        raiz.setBottom(status); // status no rodapé

        stage.setTitle("Agenda de Contatos"); // título da janela
        stage.setScene(new Scene(raiz, 760, 560)); // cenário com 760x560 pixels
        stage.show(); // mostra a janela

        carregarContatos(); // preenche a tabela com os dados do MySQL
    }

    private GridPane montarFormulario() { // monta o formulário de cadastro
        GridPane form = new GridPane(); // grade de linhas e colunas
        form.setHgap(10); // espaço horizontal entre células
        form.setVgap(8); // espaço vertical entre células
        form.setPadding(new Insets(12)); // margem interna do formulário

        form.add(new Label("Nome:"), 0, 0); // rótulo na coluna 0, linha 0
        form.add(campoNome, 1, 0); // campo na coluna 1, linha 0
        form.add(new Label("Telefone:"), 0, 1); // rótulo do telefone
        form.add(campoTelefone, 1, 1); // campo do telefone
        form.add(new Label("E-mail:"), 0, 2); // rótulo do e-mail
        form.add(campoEmail, 1, 2); // campo do e-mail
        form.add(checkComercial, 1, 3); // caixa "Contato comercial"
        form.add(new Label("Empresa:"), 0, 4); // rótulo da empresa
        form.add(campoEmpresa, 1, 4); // campo da empresa

        campoEmpresa.disableProperty().bind(checkComercial.selectedProperty().not()); // binding: o campo só habilita se a caixa estiver marcada

        Button btnSalvar = new Button("Salvar"); // botão de salvar
        btnSalvar.setOnAction(e -> salvar()); // ao clicar, chama o método salvar()
        Button btnLimpar = new Button("Limpar"); // botão de limpar
        btnLimpar.setOnAction(e -> limparFormulario()); // ao clicar, chama limparFormulario()
        form.add(new HBox(10, btnSalvar, btnLimpar), 1, 5); // coloca os dois botões lado a lado

        return form; // devolve o formulário pronto
    }

    private VBox montarAreaDaTabela() { // monta a barra de busca + tabela
        campoBusca.setPromptText("Buscar por nome"); // texto de dica dentro do campo
        Button btnBuscar = new Button("Buscar"); // botão de busca
        btnBuscar.setOnAction(e -> buscar()); // ao clicar, chama buscar()
        Button btnTodos = new Button("Mostrar todos"); // botão para listar tudo de novo
        btnTodos.setOnAction(e -> carregarContatos()); // ao clicar, recarrega a lista completa
        Button btnRemover = new Button("Remover selecionado"); // botão de remover
        btnRemover.setOnAction(e -> remover()); // ao clicar, chama remover()

        HBox barra = new HBox(10, campoBusca, btnBuscar, btnTodos, btnRemover); // barra horizontal com os controles
        VBox area = new VBox(8, barra, tabela); // barra em cima, tabela embaixo
        area.setPadding(new Insets(0, 12, 0, 12)); // margem lateral
        return area; // devolve a área pronta
    }

    private void montarTabela() { // configura colunas e liga a tabela à lista observável
        TableColumn<Contato, String> colNome = new TableColumn<>("Nome"); // coluna: linhas do tipo Contato, células do tipo String
        colNome.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNome())); // de cada Contato, mostra o nome
        colNome.setPrefWidth(180); // largura preferida

        TableColumn<Contato, String> colTelefone = new TableColumn<>("Telefone"); // coluna do telefone
        colTelefone.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTelefone())); // mostra o telefone
        colTelefone.setPrefWidth(140); // largura preferida

        TableColumn<Contato, String> colEmail = new TableColumn<>("E-mail"); // coluna do e-mail
        colEmail.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getEmail())); // mostra o e-mail
        colEmail.setPrefWidth(200); // largura preferida

        TableColumn<Contato, String> colEmpresa = new TableColumn<>("Empresa"); // coluna da empresa
        colEmpresa.setCellValueFactory(cd -> new SimpleStringProperty( // só contatos comerciais têm empresa
                cd.getValue() instanceof ContatoComercial cc ? cc.getEmpresa() : "")); // comum fica em branco
        colEmpresa.setPrefWidth(180); // largura preferida

        tabela.getColumns().add(colNome); // adiciona a coluna nome
        tabela.getColumns().add(colTelefone); // adiciona a coluna telefone
        tabela.getColumns().add(colEmail); // adiciona a coluna e-mail
        tabela.getColumns().add(colEmpresa); // adiciona a coluna empresa
        tabela.setItems(dados); // liga a tabela à lista observável
        tabela.setPrefHeight(320); // altura preferida da tabela
    }

    private void carregarContatos() { // lê do banco e atualiza a tabela
        try { // protege contra falha do banco
            dados.setAll(agenda.listar()); // troca o conteúdo da lista (a tabela atualiza sozinha)
            status.setText(dados.size() + " contato(s) na agenda."); // mensagem de status
        } catch (RepositorioException e) { // erro vindo do repositório
            mostrarErro("Erro ao listar contatos: " + e.getMessage()); // avisa o usuário
        }
    }

    private void salvar() { // lê o formulário e grava o contato
        String nome = campoNome.getText().trim(); // lê o nome sem espaços nas pontas
        String telefone = campoTelefone.getText().trim(); // lê o telefone
        String email = campoEmail.getText().trim(); // lê o e-mail

        if (nome.isEmpty()) { // nome é obrigatório
            mostrarAviso("Informe o nome."); // avisa
            return; // interrompe
        }
        if (!ValidadorEmail.validar(email)) { // reaproveita a validação do console
            mostrarAviso("E-mail inválido! Precisa conter @ e ."); // avisa
            return; // interrompe
        }

        Contato contato; // tipo pai: guarda Contato ou ContatoComercial
        if (checkComercial.isSelected()) { // se marcou "comercial"
            String empresa = campoEmpresa.getText().trim(); // lê a empresa
            if (empresa.isEmpty()) { // empresa obrigatória nesse caso
                mostrarAviso("Informe a empresa."); // avisa
                return; // interrompe
            }
            contato = new ContatoComercial(nome, telefone, email, empresa); // cria o comercial
        } else { // contato comum
            contato = new Contato(nome, telefone, email); // cria o comum
        }

        try { // protege contra falha do banco
            agenda.adicionarContato(contato); // grava no MySQL
            limparFormulario(); // esvazia os campos
            carregarContatos(); // atualiza a tabela
            status.setText("Contato salvo com sucesso!"); // mensagem de status
        } catch (RepositorioException e) { // erro vindo do repositório
            mostrarErro("Erro ao salvar: " + e.getMessage()); // avisa o usuário
        }
    }

    private void buscar() { // busca por nome
        String termo = campoBusca.getText().trim(); // lê o termo
        if (termo.isEmpty()) { // busca vazia = mostrar tudo
            carregarContatos(); // recarrega a lista completa
            return; // interrompe
        }
        try { // protege contra falha do banco
            Contato achado = agenda.buscarPorNome(termo); // usa a busca que já existe
            if (achado == null) { // não encontrou
                dados.clear(); // esvazia a tabela
                status.setText("Nenhum contato com esse nome."); // mensagem de status
            } else { // encontrou
                dados.setAll(achado); // mostra só o encontrado
                status.setText("1 contato encontrado."); // mensagem de status
            }
        } catch (RepositorioException e) { // erro vindo do repositório
            mostrarErro("Erro na busca: " + e.getMessage()); // avisa o usuário
        }
    }

    private void remover() { // remove a linha selecionada
        Contato selecionado = tabela.getSelectionModel().getSelectedItem(); // contato da linha clicada (null se nenhuma)
        if (selecionado == null) { // nada selecionado
            mostrarAviso("Selecione um contato na tabela."); // avisa
            return; // interrompe
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, // janela de confirmação
                "Remover " + selecionado.getNome() + "?", ButtonType.YES, ButtonType.NO); // texto e botões Sim/Não
        confirmacao.showAndWait().ifPresent(resposta -> { // espera a resposta do usuário
            if (resposta == ButtonType.YES) { // se confirmou
                try { // protege contra falha do banco
                    agenda.remover(selecionado); // apaga do MySQL pelo id
                    carregarContatos(); // atualiza a tabela
                    status.setText("Contato removido."); // mensagem de status
                } catch (RepositorioException e) { // erro vindo do repositório
                    mostrarErro("Erro ao remover: " + e.getMessage()); // avisa o usuário
                }
            }
        });
    }

    private void limparFormulario() { // esvazia o formulário
        campoNome.clear(); // limpa o nome
        campoTelefone.clear(); // limpa o telefone
        campoEmail.clear(); // limpa o e-mail
        campoEmpresa.clear(); // limpa a empresa
        checkComercial.setSelected(false); // desmarca "comercial"
    }

    private void mostrarAviso(String mensagem) { // janela de aviso simples
        new Alert(Alert.AlertType.WARNING, mensagem).showAndWait(); // mostra e espera o OK
    }

    private void mostrarErro(String mensagem) { // janela de erro
        new Alert(Alert.AlertType.ERROR, mensagem).showAndWait(); // mostra e espera o OK
    }

    public static void main(String[] args) { // ponto de entrada normal do Java
        launch(args); // entrega o controle ao JavaFX (que chama o start)
    }
}
