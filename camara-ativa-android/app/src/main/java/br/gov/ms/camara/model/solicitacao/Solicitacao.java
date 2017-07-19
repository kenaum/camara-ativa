package br.gov.ms.camara.model.solicitacao;

import br.gov.ms.camara.model.User;

/**
 * Created by Elisa Freitas on 01/03/2016.
 */
public class Solicitacao {
    private Integer id;
    private String latitude;
    private String longitude;
    private String endereco;
    private RequestType requestType;
    private RequestStatus status;
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public RequestStatus getSituacao() {
        return status;
    }

    public void setSituacao(RequestStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}