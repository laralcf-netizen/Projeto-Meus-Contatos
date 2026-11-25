package database; 

import java.io.FileInputStream; 
import java.io.IOException; 
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException; 
import java.sql.Statement; 


import java.util.Properties; 

public class ConexaoBD { 

    private static final String ARQUIVO = "db.properties"; 

    private ConexaoBD() {
    } 

    public static Connection conectar() throws SQLException { 
        Properties p = new Properties(); 
        try (FileInputStream in = new FileInputStream(ARQUIVO)) { 
            p.load(in); 
        } catch (IOException e) { 
            throw new SQLException("Não foi possível ler " + ARQUIVO + " (está na raiz do projeto?)", e); 
        }
        return DriverManager.getConnection(p.getProperty("url"), p.getProperty("usuario"), p.getProperty("senha"));
    }

    public static void criarTabelas() throws SQLException { 
        String sql = "CREATE TABLE IF NOT EXISTS contatos (" 
                + "id INT AUTO_INCREMENT PRIMARY KEY, " 
                + "nome VARCHAR(100) NOT NULL, " 
                + "telefone VARCHAR(20), " 
                + "email VARCHAR(120) NOT NULL, " 

                + "empresa VARCHAR(100)" 
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"; 
        try (Connection conn = conectar(); Statement st = conn.createStatement()) { 
            st.execute(sql); // executa o comando
        }
    }
}
