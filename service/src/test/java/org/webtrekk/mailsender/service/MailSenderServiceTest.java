package org.webtrekk.mailsender.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.webtrekk.mailsender.configuration.MailConfiguration;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailSenderServiceTest {


    MailSenderService mailSenderService;

    // Using a "REAL" Spring JavaMailSender we can create REAL MimeMessage needed for mock object
    @Autowired
    private JavaMailSender javaMailSender;

    // This is a Spy so didn't work as JavaMailSender (Spring) because we miss the autowiring here but we can use for mocking and pass it a MimeMessage needed for test
    @Spy
    private JavaMailSender spyEmailSender;

    @Autowired
    private MailConfiguration mailConfiguration;

    private String uri  = "http://www.orimi.com/pdf-test.pdf";

    @Before
    public  void init(){
        mailSenderService = new MailSenderService(spyEmailSender, mailConfiguration);
    }


    @Test
    public void testPopulateMimeMessage() throws Exception {
        // Arrange
        final String to = "to";
        final String subject = "subject";
        final String text = "text";

        when(spyEmailSender.createMimeMessage()).thenReturn(javaMailSender.createMimeMessage());


        // Act
        final MimeMessage result = mailSenderService.populateMimeMessage(to, subject, text, uri);

        // Asssert
        assertNotEquals(null, result);
    }

    @Test
    public void SimpleMessageShouldWorkIfMailServerNotGiveAnException() throws ExecutionException, InterruptedException {
        // Arrange
        doNothing().when(spyEmailSender).send((SimpleMailMessage) any());

        //ACT
        Future<Boolean> booleanFuture = mailSenderService.sendMailMessage("to", "subject", "text");

        while (!booleanFuture.isDone()){
            waitingForAsyncCallToFinish();
        }
        boolean res = booleanFuture.get();

        //Assert
        assertTrue(res == true);
    }


    @Test
    public void MimeMessageShouldWorkIfMailServerNotGiveAnException() throws ExecutionException, InterruptedException, IOException, MessagingException {
        doNothing().when(spyEmailSender).send((MimeMessage) any());
        when(spyEmailSender.createMimeMessage()).thenReturn(javaMailSender.createMimeMessage());

        Future<Boolean> booleanFuture = mailSenderService.sendMailWithAttachment("antimo@gmail.com", "Test mail", "Content of the email ", uri);

        while (!booleanFuture.isDone()){
            waitingForAsyncCallToFinish();
        }
        boolean res = booleanFuture.get();
        assertTrue(res == true);
    }


    @Test
    public void SimpleMessageShouldFailIfMailServerFail() throws ExecutionException, InterruptedException {

        //Arrange
        doThrow(new MailSendException("Test Message")).when(spyEmailSender).send((SimpleMailMessage) any());

        // ACT
        Future<Boolean> booleanFuture = mailSenderService.sendMailMessage("to", "subject", "text");

        while (!booleanFuture.isDone()){
            waitingForAsyncCallToFinish();
        }
        boolean res = booleanFuture.get();

        // Assert
        assertTrue(res == false);
    }

    @Test
    public void MimeMessageShouldFailIfMailServerFail() throws ExecutionException, InterruptedException {

        //Arrange
        doThrow(new MailSendException("Test Message")).when(spyEmailSender).send((MimeMessage)any());
        when(spyEmailSender.createMimeMessage()).thenReturn(javaMailSender.createMimeMessage());


        Future<Boolean> booleanFuture = mailSenderService.sendMailWithAttachment("antimo@gmail.com", "Test mail", "Content of the email ", uri);

        while (!booleanFuture.isDone()){
            waitingForAsyncCallToFinish();
        }
        boolean res = booleanFuture.get();
        assertTrue(res == false);
    }


    private void waitingForAsyncCallToFinish() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
