package com.example.quiz.mail;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quiz.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI extends AsyncTask<Void,Void,Boolean>  {

    //Add those line in dependencies
    //implementation files('libs/activation.jar')
    //implementation files('libs/additionnal.jar')
    //implementation files('libs/mail.jar')

    //Need INTERNET permission

    //Variables
    @SuppressLint("StaticFieldLeak")
    private final Context mContext;

    private final String mEmail;
    private final String mSubject;
    private final String mMessage;
    @SuppressLint("StaticFieldLeak")
    private final LinearProgressIndicator progressIndicator;
    @SuppressLint("StaticFieldLeak")
    private final View divider;
    private  Button updatePassword;
    private  EditText codeInput;


    //Constructor
    public JavaMailAPI(Context mContext, String mEmail, String mSubject, String mMessage, LinearProgressIndicator progressIndicator, View divider) {
        this.mContext = mContext;
        this.mEmail = mEmail;
        this.mSubject = mSubject;
        this.mMessage = mMessage;
        this.progressIndicator = progressIndicator;
        this.divider = divider;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Show progress dialog while sending email
        progressIndicator.setVisibility(View.VISIBLE);
        divider.setVisibility(View.GONE);

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        divider.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.GONE);
        updatePassword = (Button) ((Activity) mContext).findViewById(R.id.updatePassword);
        codeInput = (EditText) ((Activity) mContext).findViewById(R.id.passwordCodeInput);
        ((Button) ((Activity) mContext).findViewById(R.id.sendCode)).setVisibility(View.GONE);
        ((TextInputLayout) ((Activity) mContext).findViewById(R.id.passwordResetInputLayout)).setVisibility(View.GONE);
        ((Button) ((Activity) mContext).findViewById(R.id.updatePassword)).setVisibility(View.VISIBLE);
        ((TextInputLayout) ((Activity) mContext).findViewById(R.id.passwordLayout)).setVisibility(View.VISIBLE);

        //Show success toast
        Toast.makeText(mContext,"Message Sent",Toast.LENGTH_SHORT).show();
    }



    @Override
    protected Boolean doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        //Authenticating the password
        Session mSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("gameetub@gmail.com", "game2021");
                    }
                });
        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(mSession);

            //Setting sender address
            mm.setFrom(new InternetAddress("gameetub@gmail.com"));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            //Adding subject
            mm.setSubject(mSubject);
            //Adding message
            mm.setText(mMessage);
            //Sending email
            Transport.send(mm);

//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            messageBodyPart.setText(message);
//
//            Multipart multipart = new MimeMultipart();
//
//            multipart.addBodyPart(messageBodyPart);
//
//            messageBodyPart = new MimeBodyPart();
//
//            DataSource source = new FileDataSource(filePath);
//
//            messageBodyPart.setDataHandler(new DataHandler(source));
//
//            messageBodyPart.setFileName(filePath);
//
//            multipart.addBodyPart(messageBodyPart);

//            mm.setContent(multipart);
                return true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}