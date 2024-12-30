package com.elite.app.builder.services;

import com.elite.app.builder.entities.Application;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String sendEmail(String to, String subject, String body, Application application) {
        var downloadLink = "link";
        String htmlTemplate = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }" +
                "    .header { font-size: 24px; font-weight: bold; margin-bottom: 20px; }" +
                "    .content { margin-bottom: 20px; }" +
                "    .button { display: inline-block; padding: 10px 20px; background-color: #007BFF; color: white; text-decoration: none; border-radius: 5px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">Your Application is Ready!</div>" +
                "        <div class=\"content\">Congratulations! Your application <strong>%s</strong> has been successfully generated.</div>" +
                "        <div class=\"content\">Click the link below to download your application:</div>" +
                "        <a href=\"%s\" class=\"button\">Download Application</a>" +
                "    </div>" +
                "</body>" +
                "</html>";

        String emailContent = String.format(htmlTemplate, application.getName(), downloadLink);

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Your Application is Ready");
            helper.setText(emailContent, true);
            mailSender.send(message);
            System.out.println("Email sent successfully to " + to);
            return  "Email sent successfully to " + to;
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            return "Error sending email: " + e.getMessage() ;
        }
     }

     public boolean sendConfirmationEmail(String to , String confirmationCode){
          String htmlTemplate = "<!DOCTYPE html>" +
                 "<html>" +
                 "<head>" +
                 "<style>" +
                 "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                 "    .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }" +
                 "    .header { font-size: 24px; font-weight: bold; margin-bottom: 20px; }" +
                 "    .content { margin-bottom: 20px; }" +
                 "    .code { font-size: 20px; font-weight: bold; color: #007BFF; padding: 10px; border: 1px dashed #ddd; border-radius: 5px; background-color: #f9f9f9; display: inline-block; }" +
                 "</style>" +
                 "</head>" +
                 "<body>" +
                 "    <div class=\"container\">" +
                 "        <div class=\"header\">Confirm Your Email Address</div>" +
                 "        <div class=\"content\">Thank you for registering with us. To complete your registration, please use the confirmation code below:</div>" +
                 "        <div class=\"code\">%s</div>" +
                 "        <div class=\"content\">If you did not request this, please ignore this email.</div>" +
                 "    </div>" +
                 "</body>" +
                 "</html>";

         String emailContent = String.format(htmlTemplate, confirmationCode);

         try {
             MimeMessage message = mailSender.createMimeMessage();
             MimeMessageHelper helper = new MimeMessageHelper(message, true);

             helper.setTo(to);
             helper.setSubject("Your Confirmation Code");
             helper.setText(emailContent, true);
             mailSender.send(message);

             System.out.println("Email sent successfully to " + to);
             return true;
         } catch (MessagingException e) {
             System.err.println("Error sending email: " + e.getMessage());
             return false;
         }

     }

    public boolean sendConfirmedEmail(String email) {

        String htmlTemplate = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }" +
                "    .header { font-size: 24px; font-weight: bold; margin-bottom: 20px; }" +
                "    .content { margin-bottom: 20px; }" +
                "    .button { display: inline-block; padding: 10px 20px; background-color: #28A745; color: white; text-decoration: none; border-radius: 5px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">Your Email is Confirmed!</div>" +
                "        <div class=\"content\">Congratulations! Your email address has been successfully confirmed, and your account is now active.</div>" +
                "        <div class=\"content\">You can now log in to your account and start using our services.</div>" +
                "        <div class=\"content\">If you have any issues, please contact support.</div>" +
                "        <div class=\"content\">" +
                "            <a href=\"%s\" class=\"button\">Go to Dashboard</a>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

        String dashboardLink = "http://localhost:4200/login"; // Link to your dashboard or user area
        String emailContent = String.format(htmlTemplate, dashboardLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Your Account is Now Active");
            helper.setText(emailContent, true);
            mailSender.send(message);
            System.out.println("Email sent successfully to " + email);
            return true;
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            return false;
        }


    }

    public boolean sendResetPwdEmail(String email, String pwd) {
        String htmlTemplate = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }" +
                "    .header { font-size: 24px; font-weight: bold; margin-bottom: 20px; }" +
                "    .content { margin-bottom: 20px; }" +
                "    .button { display: inline-block; padding: 10px 20px; background-color: #28A745; color: white; text-decoration: none; border-radius: 5px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">Your New Password</div>" +
                "        <div class=\"content\">Hello,</div>" +
                "        <div class=\"content\">Your new password has been successfully generated. You can now use it to log in to your account.</div>" +
                "        <div class=\"content\"><strong>New Password: </strong>%s</div>" + // Display new password here
                "        <div class=\"content\">Please make sure to change your password after logging in.</div>" +
                "        <div class=\"content\">" +
                "            <a href=\"%s\" class=\"button\">Go to Login</a>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

        String loginLink = "http://localhost:4200/login";
        String emailContent = String.format(htmlTemplate, pwd, loginLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Your New Password");
            helper.setText(emailContent, true);
            mailSender.send(message);
            System.out.println("Email sent successfully to " + email);
            return true;
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            return false;

        }
    }
}
