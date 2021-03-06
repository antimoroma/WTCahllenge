package org.webtrekk.mailsender.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.webtrekk.mailsender.controller.exception.InvalidURISyntaxException;
import org.webtrekk.mailsender.service.MailSenderService;

import java.net.URISyntaxException;

@RestController
public class MailSenderController {

    private final static Logger logger = LoggerFactory.getLogger(MailSenderController.class);


    @Autowired
    MailSenderService mailSenderService;


    // String to, String subject, String text, URI AttachmentURI
/*    @GetMapping("/sendMail")
    public void sendMail1(
            @RequestParam String to ,
            @RequestParam String subject,
            @RequestParam String text,
            @RequestParam(required = false) String attachmentURI
    ){

        // TODO return HTTP error code
        URI uri = null;
        try {
            uri = new URI(attachmentURI);
        } catch (URISyntaxException e) {
            throw new InvalidURISyntaxException();
        }

        mailSenderService.sendMailMessage(to,subject,text);

    }*/

    // String to, String subject, String text, URI AttachmentURI
    @PutMapping("/sendMail")
    public void sendMail(
            @RequestParam String to ,
            @RequestParam String subject,
            @RequestParam String text,
            @RequestParam(required = false) String attachmentURI
            ){

        java.net.URI uri = null;
        if (attachmentURI != null) {
            try {
                uri = new java.net.URI(attachmentURI);
                mailSenderService.sendMailWithAttachment(to,subject,text,attachmentURI);

            } catch (URISyntaxException e) {
                throw new InvalidURISyntaxException();
            }
        }

    }



}
