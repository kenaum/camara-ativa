package br.gov.ms.camara.enums;

/**
 * Created by Elisa Freitas on 01/03/2016.
 */
public enum EnumCameraOptions {
    TAKEPHOTO (1),
    CHOOSEFROMGALLERY (2);

    private Integer id;
    private EnumCameraOptions(Integer id) {
        this.id = id;
    }
    public Integer getId(){
        return id;
    }
}
