package br.gov.ms.camara.model.vereador;

import java.util.ArrayList;

/**
 * Created by Elisa Freitas on 01/03/2016.
 */
public class Vereador {
    private Integer id;
    private String vereadorNome;
    private String vereadorBiografia;
    private String vereadorFoto;
    private String vereadorPartido;
    private String vereadorTwitter;
    private String vereadorEmail;
    private String vereadorFacebook;
    private String vereadorSite;
    private ArrayList<VereadorTelefone> vereadorTelefone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVereadorNome() {
        return vereadorNome;
    }

    public void setVereadorNome(String vereadorNome) {
        this.vereadorNome = vereadorNome;
    }

    public String getVereadorBiografia() {
        return vereadorBiografia;
    }

    public void setVereadorBiografia(String vereadorBiografia) {
        this.vereadorBiografia = vereadorBiografia;
    }

    public String getVereadorFoto() {
        return vereadorFoto;
    }

    public void setVereadorFoto(String vereadorFoto) {
        this.vereadorFoto = vereadorFoto;
    }

    public String getVereadorPartido() {
        return vereadorPartido;
    }

    public void setVereadorPartido(String vereadorPartido) {
        this.vereadorPartido = vereadorPartido;
    }

    public String getVereadorTwitter() {
        return vereadorTwitter;
    }

    public void setVereadorTwitter(String vereadorTwitter) {
        this.vereadorTwitter = vereadorTwitter;
    }

    public String getVereadorEmail() {
        return vereadorEmail;
    }

    public void setVereadorEmail(String vereadorEmail) {
        this.vereadorEmail = vereadorEmail;
    }

    public String getVereadorFacebook() {
        return vereadorFacebook;
    }

    public void setVereadorFacebook(String vereadorFacebook) {
        this.vereadorFacebook = vereadorFacebook;
    }

    public String getVereadorSite() {
        return vereadorSite;
    }

    public void setVereadorSite(String vereadorSite) {
        this.vereadorSite = vereadorSite;
    }

    public ArrayList<VereadorTelefone> getVereadorTelefone() {
        return vereadorTelefone;
    }

    public void setVereadorTelefone(ArrayList<VereadorTelefone> vereadorTelefone) {
        this.vereadorTelefone = vereadorTelefone;
    }

    @Override
    public String toString() {
        return getVereadorNome().toString();
    }
}
