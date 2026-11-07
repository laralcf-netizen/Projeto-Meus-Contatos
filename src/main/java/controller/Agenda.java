/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Contato;
import repository.Repositorio;
import utils.OperacoesContato;


public class Agenda <T extends Contato> {
    private final Repositorio<T> repositorio;
    
    public Agenda (Repositorio<T> repositorio){
    this.repositorio = repositorio;
    }
    
    public void adicionarContato (T contato){
        repositorio.adicionar(contato);
    }
    public void listarContatos(){
        OperacoesContato.imprimir(repositorio.listar());
  }
    
    public T buscarPorNome(String nome){
    return OperacoesContato.buscarPorNome(repositorio.listar(), nome);
    }

    public void removerContato(String nome) {
       if (OperacoesContato.removerPorNome(repositorio, nome)){
            
        System.out.println("Contato removido com sucesso!");
        } else{
         System.out.println("Contato não encontrado");
        }
    }
}
