package utils; 

import java.util.List; 
import model.Contato; 
import model.ContatoComercial; 
import repository.Repositorio; 

public final class OperacoesContato { 

    
    public static <T extends Contato> T buscarPorNome(List<T> lista, String nome) { 
        for (T c : lista) { 
            if (c.getNome().equalsIgnoreCase(nome)) { 
                return c; 
            }
        }
        return null; 
    }

    public static <T extends Contato> boolean removerPorNome(Repositorio<T> repo, String nome) { 
        T alvo = buscarPorNome(repo.listar(), nome); 
        return alvo != null && repo.remover(alvo); 
    }

    public static void contar(List<?> lista) { 
        System.out.println("Total: " + lista.size()); 
    }

    public static void imprimir(List<? extends Contato> lista) { 
        for (Contato c : lista) { 
            System.out.println(c);
        }
    }

    public static void copiarComerciais(List<? extends Contato> origem, List<? super ContatoComercial> destino) { 
        for (Contato c : origem) { 
            if (c instanceof ContatoComercial cc) { 
                destino.add(cc); }
        }
    }
}
