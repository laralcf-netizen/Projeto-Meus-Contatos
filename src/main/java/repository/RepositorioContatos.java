package repository;

import java.util.ArrayList;
import java.util.List;
import model.Contato;


public class RepositorioContatos<T extends Contato> implements Repositorio<T> {
    private final List<T> itens = new ArrayList<>();
    
    @Override
    public void adicionar(T item) { itens.add(item); }

    @Override
    public List<T> listar() {
       return new ArrayList<>(itens); 
    }

    @Override
    public boolean remover(T item) {
        return itens.remove(item);
    }
    
}
