package exceptions; // mesmo pacote da ContatoException

public class RepositorioException extends RuntimeException { // não verificada: não obriga try/catch em quem chama
    public RepositorioException(String mensagem, Throwable causa) { // guarda também a causa original
        super(mensagem, causa); // repassa para a classe pai
    }
}
