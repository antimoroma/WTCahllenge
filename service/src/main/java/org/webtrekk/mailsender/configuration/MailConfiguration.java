package org.webtrekk.mailsender.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {

    @Value("${user.role}")
    private String role;


    @Value("${mail.server.host}")
    private String host;

    @Value("${mail.server.port}")
    private int port;

    @Value("${mail.user.name}")
    private String userName;

    @Value("${mail.user.password}")
    private String password;

    @Value("${mail.retry.number}")
    int retryNumber;

    @Value("${mail.retry.pause}")
    int retryPause;


    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(userName);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
/*
        Thoe Properties are required when using gmail server for example
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
*/

        return mailSender;
    }


    public int getRetryNumber() {
        return retryNumber;
    }

    public int getRetryPause() {
        return retryPause;
    }
}
