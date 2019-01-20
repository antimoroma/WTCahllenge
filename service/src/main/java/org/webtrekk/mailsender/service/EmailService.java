package org.webtrekk.mailsender.service;


import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.Future;

public interface EmailService {

    @Async("ThreadPoolMailSender")
    Future<Boolean> sendMailWithAttachment(String to, String subject, String text, String AttachmentURI);
}

