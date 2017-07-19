package br.gov.ms.camara.enums;

/**
 * Created by Elisa Freitas on 01/03/2016.
 */
public enum EnumFragmentScreens {
    SOLICITACOES (1),
    NOTICIASCAMARA (2),
    VEREADORES (3),
    NOTICIACONSULTADA (4),
    SOLICITACAOCONSULTADA(5),
    VEREADORCONSULTADO(6),
    COMOFUNCIONA(7);

    private Integer id;
    private EnumFragmentScreens(Integer id) {
        this.id = id;
    }
    public Integer getId(){
        return id;
    }
}
