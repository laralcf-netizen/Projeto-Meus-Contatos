/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.Repositorio to edit this template
 */
package repository;

import java.util.List;

public interface Repositorio<T> {
    void adicionar (T item);
    List<T> listar();
    boolean remover (T item);
}
