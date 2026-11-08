package ui;

import controller.Agenda; // a agenda que já usamos no console
import exceptions.RepositorioException; // erro traduzido do banco (não verificada)
import javafx.beans.property.SimpleStringProperty; // valor de texto "observável" para as células
import javafx.collections.FXCollections; // fábrica de listas observáveis
import javafx.collections.ObservableList; // lista que avisa a tabela quando muda
import javafx.fxml.FXML; // marca o que o FXML enxerga (campos e métodos)
import javafx.scene.control.Alert; // janelas de aviso
import javafx.scene.control.ButtonType; // botões padrão das janelas de aviso (Sim/Não)
import javafx.scene.control.CheckBox; // caixa de seleção
import javafx.scene.control.Label; // texto fixo
import javafx.scene.control.TableColumn; // coluna da tabela
import javafx.scene.control.TableView; // a tabela
import javafx.scene.control.TextField; // campo de texto
import model.Contato; // contato comum
import model.ContatoComercial; // contato com empresa
import repository.RepositorioContatosJDBC; // repositório ligado ao MySQL
import utils.ValidadorEmail; // validação de e-mail que já existe

public class AgendaController { // controla os eventos da tela AgendaView.fxml

    private final Agenda<Contato> agenda = new Agenda<>(new RepositorioContatosJDBC()); // a mesma Agenda do console e do AgendaFX
    private final ObservableList<Contato> dados = FXCollections.observableArrayList(); // lista observável: a tabela atualiza sozinha

    @FXML private TextField campoNome; // ligado ao fx:id="campoNome" do FXML (o nome do campo tem que ser idêntico)
    @FXML private TextField campoTelefone; // ligado ao fx:id="campoTelefone"
    @FXML private TextField campoEmail; // ligado ao fx:id="campoEmail"
    @FXML private CheckBox checkComercial; // ligado ao fx:id="checkComercial"
    @FXML private TextField campoEmpresa; // ligado ao fx:id="campoEmpresa"
    @FXML private TextField campoBusca; // ligado ao fx:id="campoBusca"
    @FXML private TableView<Contato> tabela; // ligado ao fx:id="tabela" (generics: linhas do tipo Contato)
    @FXML private TableColumn<Contato, String> colNome; // coluna do nome (linhas Contato, células String)
    @FXML private TableColumn<Contato, String> colTelefone; // coluna do telefone
    @FXML private TableColumn<Contato, String> colEmail; // coluna do e-mail
    @FXML private TableColumn<Contato, String> colEmpresa; // coluna da empresa
    @FXML private Label status; // ligado ao fx:id="status"

    @FXML // o FXMLLoader chama este método sozinho, depois de preencher os campos acima
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
