package repository; // mesmo pacote do repositório em memória

import database.ConexaoBD; // nossa conexão
import exceptions.RepositorioException; // exceção traduzida
import java.sql.Connection; // conexão aberta
import java.sql.PreparedStatement; // SQL com parâmetros
import java.sql.ResultSet; // linhas devolvidas pelo SELECT
import java.sql.SQLException; // erro de banco
import java.sql.Types; // tipos SQL (usado para gravar NULL)
import java.util.ArrayList; // lista de retorno
import java.util.List; // interface List
import model.Contato; // contato comum
import model.ContatoComercial; // contato com empresa

public class RepositorioContatosJDBC implements Repositorio<Contato> { // sem <T>: precisa criar objetos

    @Override // contrato da interface
    public void adicionar(Contato c) { // grava no banco
        String sql = "INSERT INTO contatos (nome, telefone, email, empresa) VALUES (?, ?, ?, ?)"; // ? = parâmetros (evita SQL Injection)
        try (Connection conn = ConexaoBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) { // fecha sozinho
            ps.setString(1, c.getNome()); // 1º ? = nome
            ps.setString(2, c.getTelefone()); // 2º ? = telefone (ajuste ao nome do seu getter)
            ps.setString(3, c.getEmail()); // 3º ? = e-mail (ajuste ao nome do seu getter)
            if (c instanceof ContatoComercial cc) { // se for comercial...
                ps.setString(4, cc.getEmpresa()); // ...grava a empresa (ajuste ao nome do seu getter)
            } else { // se for comum...
                ps.setNull(4, Types.VARCHAR); // ...grava NULL na coluna empresa
            }
            ps.executeUpdate(); // executa o INSERT
        } catch (SQLException e) { // algo deu errado no banco
            throw new RepositorioException("Erro ao salvar contato", e); // traduz a exceção
        }

    }

    @Override // contrato da interface
    public List<Contato> listar() { // lê do banco
        List<Contato> lista = new ArrayList<>(); // resultado
        String sql = "SELECT id, nome, telefone, email, empresa FROM contatos"; // sem parâmetros
        try (Connection conn = ConexaoBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) { // executa o SELECT
            while (rs.next()) { // percorre linha por linha
                String empresa = rs.getString("empresa"); // pode ser null
                Contato c; // variável do tipo pai
                if (empresa == null) { // sem empresa = contato comum
                    c = new Contato(rs.getString("nome"), rs.getString("telefone"), rs.getString("email")); // cria o comum
                } else { // com empresa = comercial
                    c = new ContatoComercial(rs.getString("nome"), rs.getString("telefone"), rs.getString("email"), empresa); // cria o comercial
                }
                c.setId(rs.getInt("id")); // guarda o id vindo do banco
                lista.add(c); // põe na lista
            }
        } catch (SQLException e) { // algo deu errado
            throw new RepositorioException("Erro ao listar contatos", e); // traduz a exceção
        }
        return lista; // devolve os contatos
    }

    @Override // contrato da interface
    public boolean remover(Contato c) { // solução do exercício
        String sql = "DELETE FROM contatos WHERE id = ?"; // apaga pelo id
        try (Connection conn = ConexaoBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) { // fecha sozinho
            ps.setInt(1, c.getId()); // define o id a apagar
            return ps.executeUpdate() > 0; // true se apagou ao menos uma linha
        } catch (SQLException e) { // algo deu errado
            throw new RepositorioException("Erro ao remover contato", e); // traduz a exceção

        }
    }
}
