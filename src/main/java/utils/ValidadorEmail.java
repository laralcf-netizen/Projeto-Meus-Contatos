/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

public class ValidadorEmail {
    public static boolean validar(String email) {
    return email.contains("@") && email.contains(".") ;
    }
}
