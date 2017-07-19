package br.gov.ms.camara.model.solicitacao;

/**
 * Created by rodolfoortale on 18/03/16.
 */
public class RequestType {
    private Integer solicitacao_id;
    private String nomeSolicitacao;
    private String descSolicitacao;
    private String imgSolicitacao;

    public Integer getSolicitacao_id() {
        return solicitacao_id;
    }

    public void setSolicitacao_id(Integer solicitacao_id) {
        this.solicitacao_id = solicitacao_id;
    }

    public String getNomeSolicitacao() {
        return nomeSolicitacao;
    }

    public void setNomeSolicitacao(String nomeSolicitacao) {
        this.nomeSolicitacao = nomeSolicitacao;
    }

    public String getDescSolicitacao() {
        return descSolicitacao;
    }

    public void setDescSolicitacao(String descSolicitacao) {
        this.descSolicitacao = descSolicitacao;
    }

    public String getImgSolicitacao() {
        return imgSolicitacao;
    }

    public void setImgSolicitacao(String imgSolicitacao) {
        this.imgSolicitacao = imgSolicitacao;
    }
}
