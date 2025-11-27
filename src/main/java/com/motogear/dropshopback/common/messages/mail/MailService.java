package com.motogear.dropshopback.common.messages.mail;

public interface MailService {
    void sendEmail(String to, String subject, String body);
}
