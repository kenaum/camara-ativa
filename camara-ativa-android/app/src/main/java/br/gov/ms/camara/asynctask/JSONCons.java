package br.gov.ms.camara.asynctask;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by rodolfoortale on 04/03/16.
 */
public class JSONCons {
    // Main URLCAMARA
    private static String URLCAMARA = "http://www.camara.ms.gov.br/";

    // Main URLRODOLFOORTALE
    private static String URLRODOLFOORTALE = "http://rodolfoortale.com.br/camaraativa/camara_ativa_ws_old/camara-ativa/";
    //private static String URLRODOLFOORTALE = "http://192.168.1.102/appcamara/camara_ativa_ws_old_/camara-ativa/";
    //rodolfoortale.com.br/camaraativa/camara_ativa_ws_old/camara-ativa/?tag=forgotPassword&user=ortale22@gmail.com

    // Params Camara
    private static String URLFINDLASTNOTICIA = URLCAMARA + "?secao=noticias&qtd=1&_pg=1&formato=json";
    private static String URLFINDNOTICIA = URLCAMARA + "?secao=noticias&qtd=100&_pg=1&formato=json";

    // Params Rodolfo Ortale
    private static String URLLOGINUSER = URLRODOLFOORTALE + "?tag=login";
    private static String URLSAVEUSER = URLRODOLFOORTALE + "?tag=register";

    private static String URLFINDSOLICITACOES = URLRODOLFOORTALE + "?tag=findRequestTypes";
    private static String URLFINDVEREADORES = URLRODOLFOORTALE + "?tag=findAldermen";

    /*
    * Default fields
    */
    public static final String keyId        = "id";
    public static final String keyIdUser    = "id_user";
    public static final String keyUser      = "user";
    public static final String keyPassword  = "password";
    public static final String keyNome      = "nome";
    public static final String keyTelefone  = "telefone";
    public static final String keyIsBlocked = "is_blocked";
    public static final String keyRequestImage = "request_image";
    public static final String keySuccess   = "success";
    public static final String keyError     = "error";

    public static final String keyTag              = "tag";
    public static final String keyRegister         = "register";
    public static final String keyFindRequestTypes = "findRequestTypes";
    public static final String keyCheckUser        = "checkUser";
    public static final String keyFindAldermen     = "findAldermen";
    public static final String keySaveRequest      = "saveRequest";
    public static final String keyLogin            = "login";
    public static final String keyForgotPassword   = "forgotPassword";

    public static final String keyLatitude = "latitude";
    public static final String keyLongitude = "longitude";
    public static final String keyEndereco = "endereco";
    public static final String keyRequestType = "requestType";
    public static final String keyStatus = "status";

    public static String getBaseURL() {
        return URLRODOLFOORTALE;
    }

    public static String getURLLogin() {
        return URLLOGINUSER;
    }

    public static String getURLSaveUser() {
        return URLSAVEUSER;
    }

    public static String getURLFindLastNoticia() {
        return URLFINDLASTNOTICIA;
    }

    public static String getURLFindNoticia() { return URLFINDNOTICIA; }

    public static String getURLFindSolicitacao() { return URLFINDSOLICITACOES; }

    public static String getURLFindVereadores() { return URLFINDVEREADORES; }

    private static final String hashToken = "Câmara Ativa20112016";

    public static final String getToken() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] bytesOfMessage = JSONCons.hashToken.getBytes("UTF-8");
            byte[] thedigest = md.digest(bytesOfMessage);

            return thedigest.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
