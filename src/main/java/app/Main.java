package app;

import controller.Agenda;
import java.util.Scanner;
import model.Contato;
import model.ContatoComercial;
import utils.ValidadorEmail;
import database.ConexaoBD; 
import java.sql.SQLException; 
import repository.RepositorioContatosJDBC; 

public class Main {
    public static void main (String[] args){
    Scanner scanner = new Scanner(System.in);
    
        try { 
            ConexaoBD.criarTabelas(); 
        } catch (SQLException e) { 
            System.out.println("Erro ao preparar o banco: " + e.getMessage()); 
            return; 
        }

        Agenda<Contato> agenda = new Agenda<>(new RepositorioContatosJDBC()); 
    
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
                
                System.out.println("É contato comercial? (s/n): "); 
                String resposta = scanner.nextLine(); 

                Contato contato; 

                if (resposta.equalsIgnoreCase("s")) { 
                    System.out.println("Empresa: "); 
                    String empresa = scanner.nextLine(); 
                    contato = new ContatoComercial(nome, telefone, email, empresa); 
                } else { 
                    contato = new Contato(nome, telefone, email); 
                }

                agenda.adicionarContato(contato); 
                
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


