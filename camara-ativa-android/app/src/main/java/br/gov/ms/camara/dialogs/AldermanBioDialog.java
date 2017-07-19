package br.gov.ms.camara.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import br.gov.ms.camara.model.vereador.Vereador;
import br.gov.ms.camara.util.FormUtil;

/**
 * Created by rodolfoortale on 18/03/16.
 */
public class AldermanBioDialog extends Dialog {
    private final String TAG = "AldermanBioDialog";

    private Activity activity;
    private Vereador vereador;

    private Dialog dialog;

    private TextView lbTexto;

    private ImageButton btnClose;

    public AldermanBioDialog(final Activity activity, Vereador vereador) {
        super(activity);

        this.activity = activity;
        this.vereador = vereador;

        dialog = FormUtil.getDefaultDialogProperties(this, br.gov.ms.camara.R.layout.dialog_alderman_bio);

        String strTexto = vereador.getVereadorBiografia();

        btnClose = (ImageButton) dialog.findViewById(br.gov.ms.camara.R.id.btnClose);
        lbTexto  = (TextView) dialog.findViewById(br.gov.ms.camara.R.id.lbTexto);
        lbTexto.setText(Html.fromHtml(strTexto));
        lbTexto.setGravity(Gravity.LEFT | Gravity.RIGHT | Gravity.CENTER);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
