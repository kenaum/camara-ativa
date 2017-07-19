package br.gov.ms.camara.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.LinearLayout;

/**
 * Created by rodolfoortale on 17/03/16.
 */
public class FormUtil {
    public static Boolean debug = false;

    /*
     * Método utilizado para retornar propriedades padrões do dialog
     * @params:
     *  - dialog  = instância do dialog a ser utilizado
     *  - context = contexto do dialog a ser utilizado
     *  - layout  = id do layout do dialog
     */
    public static Dialog getDefaultDialogProperties(Dialog dialog, int layout) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        //dialog.setCancelable(FormUtil.debug);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        return dialog;
    }
}
