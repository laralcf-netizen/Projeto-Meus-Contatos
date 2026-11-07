package app; // mesmo pacote do Main

import database.ConexaoBD; // nossa conexão
import java.sql.Connection; // conexão aberta
import java.sql.SQLException; // erro de banco

public class TesteConexao { // só para testar (pode apagar depois)

    public static void main(String[] args) { // ponto de entrada
        try (Connection conn = ConexaoBD.conectar()) { // abre e fecha sozinha
            System.out.println("Conectado ao MySQL " + conn.getMetaData().getDatabaseProductVersion()); // mostra a versão do servidor
        } catch (SQLException e) { // qualquer falha
            System.out.println("Erro: " + e.getMessage()); // mensagem principal
            e.printStackTrace(); // detalhes completos (copie e cole aqui se falhar)
        }
    }
}
