package br.gov.ms.camara.util;

import android.app.Activity;
import android.app.Application;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by rodolfoortale on 17/03/16.
 */
public class CustomToast extends Toast {
    private static Toast toast;
    private static LayoutInflater inflater;
    private static TextView lbMessage;

    public static final int TOAST_ERROR   = 1;
    public static final int TOAST_WARNING = 2;
    public static final int TOAST_SUCCESS = 3;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param activity The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public CustomToast(Activity activity) {
        super(activity);
    }

    public static void show(Activity activity, String message, int messageType, int duration) {
        toast    = new Toast(activity);
        inflater = activity.getLayoutInflater();

        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(duration);

        switch (messageType) {
            case TOAST_ERROR:
                showError(message);
                break;
            case TOAST_WARNING:
                showWarning(message);
                break;
            case TOAST_SUCCESS:
                showSucess(message);
                break;
        }
    }

    private static void showError(String message) {
        View customToastRoot = inflater.inflate(br.gov.ms.camara.R.layout.red_toast, null);

        lbMessage = (TextView) customToastRoot.findViewById(br.gov.ms.camara.R.id.lbMessage);
        lbMessage.setText(message);

        toast.setView(customToastRoot);
        toast.show();
    }

    private static void showWarning(String message) {
        View customToastRoot = inflater.inflate(br.gov.ms.camara.R.layout.yellow_toast, null);

        lbMessage = (TextView) customToastRoot.findViewById(br.gov.ms.camara.R.id.lbMessage);
        lbMessage.setText(message);

        toast.setView(customToastRoot);
        toast.show();
    }

    private static void showSucess(String message) {
        View customToastRoot = inflater.inflate(br.gov.ms.camara.R.layout.blue_toast, null);

        lbMessage = (TextView) customToastRoot.findViewById(br.gov.ms.camara.R.id.lbMessage);
        lbMessage.setText(message);

        toast.setView(customToastRoot);
        toast.show();
    }
}
