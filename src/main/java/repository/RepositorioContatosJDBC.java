package repository; 

import database.ConexaoBD; 
import exceptions.RepositorioException; 
import java.sql.Connection; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Types; 
import java.util.ArrayList; 
import java.util.List; 
import model.Contato; 
import model.ContatoComercial; 

public class RepositorioContatosJDBC implements Repositorio<Contato> { 

    @Override 
    public void adicionar(Contato c) { 
        String sql = "INSERT INTO contatos (nome, telefone, email, empresa) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) { 
            ps.setString(1, c.getNome()); 
            ps.setString(2, c.getTelefone()); 
            ps.setString(3, c.getEmail()); 
            if (c instanceof ContatoComercial cc) { 
                ps.setString(4, cc.getEmpresa()); 
            } else { 
                ps.setNull(4, Types.VARCHAR); 
            }
            ps.executeUpdate(); 
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao salvar contato", e); 
        }

    }

    @Override 
    public List<Contato> listar() { 
        List<Contato> lista = new ArrayList<>(); 
        String sql = "SELECT id, nome, telefone, email, empresa FROM contatos"; 
        try (Connection conn = ConexaoBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) { 
            while (rs.next()) { 
                String empresa = rs.getString("empresa"); 
                Contato c; 
                if (empresa == null) { 
                    c = new Contato(rs.getString("nome"), rs.getString("telefone"), rs.getString("email")); 
                } else { 
                    c = new ContatoComercial(rs.getString("nome"), rs.getString("telefone"), rs.getString("email"), empresa); 
                }
                c.setId(rs.getInt("id")); 
                lista.add(c); 
            }
        } catch (SQLException e) { 
            throw new RepositorioException("Erro ao listar contatos", e); 
        }
        return lista;
    }

    @Override 
    public boolean remover(Contato c) { 
        String sql = "DELETE FROM contatos WHERE id = ?"; 
        try (Connection conn = ConexaoBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) { 
            ps.setInt(1, c.getId()); 
            return ps.executeUpdate() > 0; 
        } catch (SQLException e) { 
            throw new RepositorioException("Erro ao remover contato", e); 

        }
    }
}
