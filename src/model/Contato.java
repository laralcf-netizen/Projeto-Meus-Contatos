package model;

public class Contato {
    private String nome;
    private String telefone;
    private String email;
    private int id;
    
    public Contato (String nome, String telefone, String email) {
     //this.nome, por exemplo, se refere ao atributo da classe Contato; nome é o parâmetro, se refere ao parametro do metodo construtor.   
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
       }
 

    public int getId() { // lê o id
        return id; // devolve o valor
    }

    public void setId(int id) { // define o id (o repositório usa ao ler do banco)
        this.id = id; // guarda o valor
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString(){
        return "Nome: " + nome
                + " Telefone: " + telefone
                + " E-mail: " + email;
    }
}



