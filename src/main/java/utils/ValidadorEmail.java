package utils;

public class ValidadorEmail {
    public static boolean validar(String email) {
    return email.contains("@") && email.contains(".") ;
    }
}
