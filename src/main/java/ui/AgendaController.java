package ui;

import controller.Agenda; 
import exceptions.RepositorioException; 
import javafx.beans.property.SimpleStringProperty; 
import javafx.collections.FXCollections; 
import javafx.collections.ObservableList; 
import javafx.fxml.FXML; 
import javafx.scene.control.Alert; 
import javafx.scene.control.ButtonType; 
import javafx.scene.control.CheckBox; 
import javafx.scene.control.Label; 
import javafx.scene.control.TableColumn; 
import javafx.scene.control.TableView; 
import javafx.scene.control.TextField; 
import model.Contato; 
import model.ContatoComercial; 
import repository.RepositorioContatosJDBC; 
import utils.ValidadorEmail; 

public class AgendaController { 

    private final Agenda<Contato> agenda = new Agenda<>(new RepositorioContatosJDBC()); 
    private final ObservableList<Contato> dados = FXCollections.observableArrayList(); 

    @FXML private TextField campoNome; 
    @FXML private TextField campoTelefone; 
    @FXML private TextField campoEmail; 
    @FXML private CheckBox checkComercial; 
    @FXML private TextField campoEmpresa; 
    @FXML private TextField campoBusca; 
    @FXML private TableView<Contato> tabela; 
    @FXML private TableColumn<Contato, String> colNome; 
    @FXML private TableColumn<Contato, String> colTelefone; 
    @FXML private TableColumn<Contato, String> colEmail; 
    @FXML private TableColumn<Contato, String> colEmpresa; 
    @FXML private Label status;

    @FXML 
    private void initialize() { // aqui configuramos o que o FXML não consegue descrever
        colNome.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNome())); // de cada Contato, mostra o nome
        colTelefone.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTelefone())); // mostra o telefone
        colEmail.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getEmail())); // mostra o e-mail
        colEmpresa.setCellValueFactory(cd -> new SimpleStringProperty( // só contatos comerciais têm empresa
                cd.getValue() instanceof ContatoComercial cc ? cc.getEmpresa() : "")); // comum fica em branco
        tabela.setItems(dados); // liga a tabela à lista observável
        campoEmpresa.disableProperty().bind(checkComercial.selectedProperty().not()); // o campo só habilita com a caixa marcada
        carregarContatos(); // preenche a tabela com os dados do MySQL
    }

    @FXML // chamado pelo botão "Mostrar todos" (onAction="#carregarContatos") e pelo initialize
    private void carregarContatos() { // lê do banco e atualiza a tabela
        try { // protege contra falha do banco
            dados.setAll(agenda.listar()); // troca o conteúdo da lista (a tabela atualiza sozinha)
            status.setText(dados.size() + " contato(s) na agenda."); // mensagem de status
        } catch (RepositorioException e) { // erro vindo do repositório
            mostrarErro("Erro ao listar contatos: " + e.getMessage()); // avisa o usuário
        }
    }

    @FXML // chamado pelo botão "Salvar" (onAction="#salvar")
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

    @FXML // chamado pelo botão "Buscar" (onAction="#buscar")
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

    @FXML // chamado pelo botão "Remover selecionado" (onAction="#remover")
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

    @FXML // chamado pelo botão "Limpar" (onAction="#limparFormulario")
    private void limparFormulario() { // esvazia o formulário
        campoNome.clear(); // limpa o nome
        campoTelefone.clear(); // limpa o telefone
        campoEmail.clear(); // limpa o e-mail
        campoEmpresa.clear(); // limpa a empresa
        checkComercial.setSelected(false); // desmarca "comercial"
    }

    private void mostrarAviso(String mensagem) { // janela de aviso simples (não é ligada ao FXML)
        new Alert(Alert.AlertType.WARNING, mensagem).showAndWait(); // mostra e espera o OK
    }

    private void mostrarErro(String mensagem) { // janela de erro (não é ligada ao FXML)
        new Alert(Alert.AlertType.ERROR, mensagem).showAndWait(); // mostra e espera o OK
    }
}
