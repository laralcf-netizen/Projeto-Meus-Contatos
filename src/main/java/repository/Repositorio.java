package repository;

import java.util.List;

public interface Repositorio<T> {
    void adicionar (T item);
    List<T> listar();
    boolean remover (T item);
}
