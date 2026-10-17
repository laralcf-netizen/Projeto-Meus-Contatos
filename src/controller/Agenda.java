/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.ArrayList;
import model.Contato;


public class Agenda {
    private ArrayList<Contato> contatos = new ArrayList<>();
    
    public void adicionarContato (Contato contato){
        contatos.add(contato);
    }
    public void listarContatos(){
    for (Contato contato : contatos){
    System.out.println(contato);
    }
  }
    
    public Contato buscarPorNome(String nome){
        for (Contato contato : contatos){
            if (contato.getNome().equalsIgnoreCase(nome)){
            return contato;
            }     
        }
        return null;
    }

    public void removerContato(String nome) {
        
        Contato contato = buscarPorNome(nome);
        
        if (contato != null){
            contatos.remove(contato);
            
        System.out.println("Contato removido com sucesso!");
        } else{
         System.out.println("Contato não encontrado");
        }
    }
}
