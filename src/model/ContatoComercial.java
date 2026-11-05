
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class ContatoComercial extends Contato{
    
    private String empresa;
    
    public ContatoComercial(String nome, String telefone, String email, String empresa) {
        super(nome, telefone, email);
        this.empresa = empresa;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
    
    @Override // sobrescreve o toString herdado de Contato
    public String toString() { // texto mostrado no "Listar"
        return super.toString() + " Empresa: " + getEmpresa(); // reaproveita o texto do pai e acrescenta a empresa (ajuste ao nome do seu getter)
    }
    
}
