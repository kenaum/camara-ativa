package br.gov.ms.camara.model;

/**
 * Created by Elisa Freitas on 01/03/2016.
 */
public class Noticia {
    private Integer pg;
    private Integer pgs;
    private Integer noticia_id;
    private String data;
    private String titulo;
    private String resumo;
    private String link;

    public Integer getPg() {
        return pg;
    }

    public void setPg(Integer pg) {
        this.pg = pg;
    }

    public Integer getPgs() {
        return pgs;
    }

    public void setPgs(Integer pgs) {
        this.pgs = pgs;
    }

    public Integer getNoticia_id() {
        return noticia_id;
    }

    public void setNoticia_id(Integer noticia_id) {
        this.noticia_id = noticia_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
