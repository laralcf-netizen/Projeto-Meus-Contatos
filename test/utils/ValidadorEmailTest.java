package utils; // mesmo pacote da classe testada

import static org.junit.Assert.assertFalse; // JUnit 4: a classe é Assert (antes era Assertions)
import static org.junit.Assert.assertTrue; // verifica se o resultado é true
import org.junit.Test; // JUnit 4: org.junit.Test (antes era org.junit.jupiter.api.Test)

public class ValidadorEmailTest { // convenção: NomeDaClasse + Test

    @Test // marca o método como teste
    public void deveAceitarEmailValido() { // JUnit 4: o método precisa ser public
        assertTrue(ValidadorEmail.validar("ana@email.com")); // e-mail correto deve ser aceito
    }

    @Test // segundo teste
    public void deveRecusarEmailSemArroba() { // caso de erro 1
        assertFalse(ValidadorEmail.validar("anaemail.com")); // sem @ deve ser recusado
    }

    @Test // terceiro teste
    public void deveRecusarEmailSemPonto() { // caso de erro 2
        assertFalse(ValidadorEmail.validar("ana@email")); // sem ponto deve ser recusado
    }
}
