package com.android.documentationrecordviafingerprint.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomToast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailService extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public MailService(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) {
        try {
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            Transport.send(message);
        } catch (Exception e) {
            Log.d("mylog", "Error in sending: " + e.toString());
        }
    }

    public static class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }

    public static void sendMessage(final Activity activity, final String recipient, final String new_key) {
        final ProgressDialog dialog = new ProgressDialog(activity, R.style.MyDailogTheme);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait . . .");
        dialog.show();
        final String username = activity.getResources().getString(R.string.un);
        final String password = activity.getResources().getString(R.string.up);
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MailService sender = new MailService(username, password);
                    sender.sendMail("Your New Password from Notes via Fingerprints",
                            "This is your new password : '" + new_key + "' please must change it after sign in.",
                            username,
                            recipient);
                    dialog.dismiss();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new CustomMsgDialog(activity, "PASSWORD SENT!", "New password send to your Email address, please check your email and must change this new password after Sign in.");
                        }
                    });
                } catch (final Exception e) {
                    dialog.dismiss();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToast.makeToast(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        });
        sender.start();
    }
}