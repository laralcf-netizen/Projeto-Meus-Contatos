/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import controller.Agenda;
import java.util.Scanner;
import model.Contato;
import model.ContatoComercial;
import utils.ValidadorEmail;
import repository.RepositorioContatos; 
import database.ConexaoBD; // para criar a tabela
import java.sql.SQLException; // para tratar o erro
import repository.RepositorioContatosJDBC; // repositório do banco (troque o import do RepositorioContatos)

public class Main {
    public static void main (String[] args){
    Scanner scanner = new Scanner(System.in);
    
        try { // protege a criação da tabela
            ConexaoBD.criarTabelas(); // cria a tabela contatos se ainda não existir
        } catch (SQLException e) { // banco indisponível
            System.out.println("Erro ao preparar o banco: " + e.getMessage()); // explica o problema
            return; // encerra o programa
        }

        Agenda<Contato> agenda = new Agenda<>(new RepositorioContatosJDBC()); // única linha que troca memória por banco
    
    int opcao =0;
    
    while (opcao !=5){
        System.out.println("========= AGENDA DE CONTATOS =========");
        System.out.println("1.Adicionar novo contato");
        System.out.println("2. Listar contatos");
        System.out.println("3. Buscar por nome ");
        System.out.println("4. Remover contato");
        System.out.println("5. Sair");
        
        try {
        
        System.out.println("Escolha uma opcao: ");
        opcao = scanner.nextInt();
        scanner.nextLine();
        } catch (Exception e){
         System.out.println("Digite uma das opções listadas acima!\n");
         
         scanner.nextLine();
         
         opcao = 0;
        }
        
        switch(opcao){
            case 1:
                System.out.println("Nome: ");
                String nome = scanner.nextLine();
                
                System.out.println("Telefone: ");
                String telefone = scanner.nextLine();
                
                System.out.println("Email: ");
                String email = scanner.nextLine();
                
                if(!ValidadorEmail.validar(email)) {
                
                    System.out.println("Email invalido! Precisa conter @ e . ");
                    break;
                }
                
                System.out.println("É contato comercial? (s/n): "); // pergunta nova
                String resposta = scanner.nextLine(); // lê a resposta

                Contato contato; // tipo pai: guarda Contato ou ContatoComercial

                if (resposta.equalsIgnoreCase("s")) { // respondeu sim (aceita S ou s)
                    System.out.println("Empresa: "); // pede o dado extra
                    String empresa = scanner.nextLine(); // lê a empresa
                    contato = new ContatoComercial(nome, telefone, email, empresa); // cria o filho
                } else { // qualquer outra resposta
                    contato = new Contato(nome, telefone, email); // cria o contato comum
                }

                agenda.adicionarContato(contato); // Agenda<Contato> aceita os dois tipos (polimorfismo)
                
                System.out.println("Contato adicionado com sucesso!");
                break;
                
            case 2:
                
               System.out.println("\n LISTA DE CONTATOS");
               agenda.listarContatos();
               break;
                
            case 3:  
               System.out.println("Digite o nome para buscar: ");
               String nomeBusca = scanner.nextLine();
               
               Contato encontrado = agenda.buscarPorNome(nomeBusca);
               
               if(encontrado != null){
                   System.out.println("Contato encontrado");
                   System.out.println(encontrado);
               } else{
               System.out.println("Contato não encontrado!");
               }
               break;
                
            case 4:   
               System.out.println("Informe o nome do contato para remover: ");
               String nomeRemover = scanner.nextLine();
               agenda.removerContato(nomeRemover);
               
                break;
               
            case 5:
                System.out.println("Encerrando o sistema");
                break;
                
                default:
                 System.out.println("Opção invalida. Selecione uma das opções listadas acima.");
        }
    }
    scanner.close();
   }
}


