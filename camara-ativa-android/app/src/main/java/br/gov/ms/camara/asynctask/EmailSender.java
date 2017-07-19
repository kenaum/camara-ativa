package br.gov.ms.camara.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import br.gov.ms.camara.R;
import br.gov.ms.camara.util.Mail;

/**
 * Created by rodolfoortale on 28/03/16.
 */
public class EmailSender extends AsyncTask<JSONObject, Void, Boolean> {
    private static final String TAG = "JSONObjectParser";

    private Activity activity;

    private String textBody;
    private String emailFrom;
    private String subject;
    private String filename;
    private String[] emailTo;

    public EmailSender () {

    }

    public EmailSender(Activity activity, String textBody, String emailFrom, String subject, String filename, String[] emailTo) {
        this.activity = activity;
        this.textBody = textBody;
        this.emailFrom = emailFrom;
        this.subject = subject;
        this.filename = filename;
        this.emailTo = emailTo;
    }

    @Override
    protected Boolean doInBackground(JSONObject... params) {
        //Mail mail = new Mail(activity.getString(R.string.user), activity.getString(R.string.password));
        Mail mail = new Mail(activity.getString(R.string.user), activity.getString(R.string.password));

        //String[] emailTo = { "ortale22@gmail.com" };

        //Log.d("EMAIL TO", "email: " + "ortale22@gmail.com");
        mail.setTo(emailTo);
        mail.setFrom(emailFrom);
        //Log.d("EMAIL FROM", "email: " + emailFrom);
        mail.setSubject(subject);
        mail.setBody(textBody);

        try {
            if (filename != null && !filename.isEmpty()) {
                mail.addAttachment(filename);
            }

            mail.send();
        } catch (Exception e) {
            Log.e("MailApp", "Could not send email", e);
            return false;
        }

        return true;
    }

    protected void timeoutCallback() {}
}
