package br.gov.ms.camara.model.vereador;

/**
 * Created by Elisa Freitas on 01/03/2016.
 */
public class VereadorTelefone {
    private Integer id;
    private String telefone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
