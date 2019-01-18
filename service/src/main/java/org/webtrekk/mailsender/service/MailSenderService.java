package org.webtrekk.mailsender.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.webtrekk.mailsender.configuration.MailConfiguration;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Future;

@Service
public class MailSenderService implements EmailService {

    private final static Logger logger = LoggerFactory.getLogger(MailSenderService.class);

    private final JavaMailSender emailSender;

    private int retryPause;

    private int  retryNumber;

    @Autowired
    public MailSenderService(JavaMailSender emailSender, MailConfiguration mailConfiguration){
        this.emailSender = emailSender;
        this.retryNumber = mailConfiguration.getRetryNumber();
        this.retryPause = mailConfiguration.getRetryPause();
    }


    @Async("ThreadPoolMailSender")
    @Override
    public Future<Boolean> sendMailMessage(String to, String subject, String text) {

        SimpleMailMessage message = getSimpleMailMessage(to, subject, text);

        boolean mailSent = false;

        mailSent = isMailSent(message);

        return new AsyncResult<Boolean>(mailSent);
    }

    @Async("ThreadPoolMailSender")
    @Override
    public Future<Boolean> sendMailWithAttachment(String to, String subject, String text, String attachmentURI){
        boolean mailSent= false;
        MimeMessage message;
        try {
            message = populateMimeMessage(to, subject, text, attachmentURI );
        } catch (Exception e) {
            logger.error("Can,t send mail with attachment " + e.getMessage());
            e.printStackTrace();
            return new AsyncResult<Boolean>(mailSent);
        }

        mailSent = isMailSentMimeType(message);

        return new AsyncResult<Boolean>(mailSent);
    }

    protected MimeMessage populateMimeMessage(String to, String subject, String text, String attachmentURI)  throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        File tempFile = null;

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        try {
            tempFile = getFileFromURI(attachmentURI);
            helper.addAttachment("Pdf test", tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            if (tempFile != null) tempFile.delete();
        }
        return message;
    }



    protected boolean isMailSent(SimpleMailMessage message) {
        boolean mailSent = false;
        for (int sendAttempt = 1; sendAttempt < retryNumber; sendAttempt++) {

            try {
                emailSender.send(message);
                mailSent = true;
            } catch (MailException exception) {
                logInfoAndWaitIfNeeded(sendAttempt, exception);
            }
        }
        return mailSent;
    }


    protected boolean isMailSentMimeType(MimeMessage message) {
        boolean mailSent = false;
        for (int sendAttempt = 1; sendAttempt < retryNumber; sendAttempt++) {

            try {
                emailSender.send(message);
                mailSent = true;
            } catch (MailException exception) {
                logInfoAndWaitIfNeeded(sendAttempt, exception);
            }
        }
        return mailSent;
    }

    protected void logInfoAndWaitIfNeeded(int sendAttempt, MailException exception) {
        logger.info("Exception while sending mail");
        exception.printStackTrace();
        if (exception instanceof MailSendException) {
            pause();
            logger.info("MailSendException Waiting before retrying...Number of attempt  " + sendAttempt);
        }
    }


    protected File getFileFromURI(String attachmentURI) throws IOException {
        File tempFile = File.createTempFile("document", ".pdf");
        FileUtils.copyURLToFile(
                new URL(attachmentURI),
                new File(tempFile.getName()),
                2000,
                20000);
        return tempFile;
    }

    // in case of Connection problem try to pause before retrying to send mail
    private void pause() {
        try {
            Thread.sleep(retryPause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected SimpleMailMessage getSimpleMailMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }

    protected static String getContentType(String urlString) throws IOException{
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        String contentType = connection.getContentType();
        return contentType;
    }

}
