package database; // pacote da camada de banco

import java.io.FileInputStream; // lê o arquivo de configuração
import java.io.IOException; // erro de leitura de arquivo
import java.sql.Connection; // conexão aberta
import java.sql.DriverManager; // fábrica de conexões
import java.sql.SQLException; // erro de banco
import java.sql.Statement; // executa comandos SQL


import java.util.Properties; // estrutura chave=valor

public class ConexaoBD { // utilitário de conexão

    private static final String ARQUIVO = "db.properties"; // procurado na raiz do projeto

    private ConexaoBD() {
    } // ninguém precisa instanciar

    public static Connection conectar() throws SQLException { // quem chama trata o SQLException
        Properties p = new Properties(); // guarda url, usuario e senha
        try (FileInputStream in = new FileInputStream(ARQUIVO)) { // abre o arquivo e fecha sozinho
            p.load(in); // carrega as linhas chave=valor
        } catch (IOException e) { // arquivo ausente ou ilegível
            throw new SQLException("Não foi possível ler " + ARQUIVO + " (está na raiz do projeto?)", e); // mensagem clara
        }
        return DriverManager.getConnection(p.getProperty("url"), p.getProperty("usuario"), p.getProperty("senha")); // abre a conexão
    }

    public static void criarTabelas() throws SQLException { // será usado na etapa 6
        String sql = "CREATE TABLE IF NOT EXISTS contatos (" // só cria se não existir
                + "id INT AUTO_INCREMENT PRIMARY KEY, " // MySQL usa AUTO_INCREMENT
                + "nome VARCHAR(100) NOT NULL, " // obrigatório
                + "telefone VARCHAR(20), " // opcional
                + "email VARCHAR(120) NOT NULL, " // obrigatório

                + "empresa VARCHAR(100)" // NULL para contato comum
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"; // motor padrão e acentos
        try (Connection conn = conectar(); Statement st = conn.createStatement()) { // fecha tudo sozinho
            st.execute(sql); // executa o comando
        }
    }
}
