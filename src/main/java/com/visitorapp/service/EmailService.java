package com.visitorapp.service;

import com.visitorapp.entity.EmailSetUp;
import com.visitorapp.repository.EmailSetUpRepository;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.util.Base64;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final EmailSetUpRepository emailSetUpRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(EmailSetUpRepository emailSetUpRepository) {
        this.emailSetUpRepository = emailSetUpRepository;
    }

    private JavaMailSenderImpl createMailSender() {
        EmailSetUp config = emailSetUpRepository.findFirstByOrderById().orElse(null);
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        if (config != null) {
            sender.setHost(config.getSmtpServer());
            sender.setPort(Integer.parseInt(config.getSmtpPort()));
            sender.setUsername(config.getSmtpMailId());
            sender.setPassword(config.getSmtpPassword());
        } else {
            sender.setHost("smtp.gmail.com");
            sender.setPort(587);
        }
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        props.put("mail.smtp.localhost", "3dcad-global.com");
        return sender;
    }

    public void sendInviteEmail(String to, String visitorName, String empName, String otp, String hostCompany, String plantName) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("You're Invited to Visit " + hostCompany);

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Hello " + visitorName + ","));
            b.append(EmailTemplateBuilder.text("You have been invited to visit <b>" + hostCompany + "</b> by <b>" + empName + "</b>."));
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.infoRow("Invited by", empName));
            b.append(EmailTemplateBuilder.infoRow("Company", hostCompany));
            b.append(EmailTemplateBuilder.infoRow("Invitation Code", otp));
            if (plantName != null && !plantName.isEmpty()) {
                b.append(EmailTemplateBuilder.infoRow("Location", plantName));
            }
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.text("Please use the following code at the reception to check in:"));
            b.append(EmailTemplateBuilder.otpSection("Your Check-In Code", otp));
            b.append(EmailTemplateBuilder.text("Present this code at the reception desk upon arrival to complete your check-in."));

            helper.setText(EmailTemplateBuilder.wrap("Visitor Invitation", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send invite email to {}", to, e);
        }
    }

    @Async
    public void sendAcceptanceEmailToEmp(String empEmail, String visitorName, String visitDate, String checkInOtp) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(empEmail);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Invitation Accepted - " + visitorName);

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Invitation Accepted"));
            b.append(EmailTemplateBuilder.text("<b>" + visitorName + "</b> has accepted your invitation for <b>" + visitDate + "</b>."));
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.text("Share this code with the visitor at check-in:"));
            b.append(EmailTemplateBuilder.otpSection("Check-In Code", checkInOtp));
            b.append(EmailTemplateBuilder.text("The visitor will present this code at the reception to complete their check-in."));

            helper.setText(EmailTemplateBuilder.wrap("Invitation Accepted", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send acceptance email to employee", e);
        }
    }

    public void sendAcceptanceEmailToVisitor(String to, String visitorName, String empName, String checkInOtp, String hostCompany, String plantName) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Visit Confirmed - " + hostCompany);

            String checkinLink = baseUrl + "/self-checkin?code=" + checkInOtp;

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Dear " + visitorName + ","));
            b.append(EmailTemplateBuilder.text("Your invitation to visit <b>" + hostCompany + "</b> has been confirmed."));
            b.append(EmailTemplateBuilder.text("You will be meeting <b>" + empName + "</b> during your visit."));
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.infoRow("Company", hostCompany));
            b.append(EmailTemplateBuilder.infoRow("Meeting with", empName));
            if (plantName != null && !plantName.isEmpty()) {
                b.append(EmailTemplateBuilder.infoRow("Location", plantName));
            }
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.otpSection("Your Check-In Code", checkInOtp));
            b.append(EmailTemplateBuilder.button(checkinLink, "🚀 Self Check-In Online"));
            b.append(EmailTemplateBuilder.text("Use the code above or click the button to check in online when you arrive at the office."));
            b.append(EmailTemplateBuilder.text("Present your check-in confirmation at the reception to complete your visit."));

            helper.setText(EmailTemplateBuilder.wrap("Visit Confirmed - " + hostCompany, b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Email send failed", e);
        }
    }

    public void sendCheckInEmailToVisitor(String to, String visitorName, String empName, String hostCompany, String plantName, String checkoutCode) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("You've Checked In - " + hostCompany);

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Welcome, " + visitorName + "!"));
            b.append(EmailTemplateBuilder.text("You have successfully checked in to <b>" + hostCompany + "</b>."));
            b.append("<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background:#e6fff0;border-radius:12px;-webkit-border-radius:12px;margin:16px 0'><tr><td style='padding:20px;text-align:center'>"
                + "<p style='font-size:40px;margin:0 0 8px'>✅</p>"
                + "<p style='margin:0;font-size:16px;color:#389e0d;font-weight:600'>You are now checked in!</p>"
                + "<p style='margin:8px 0 0;font-size:14px;color:#555'>Please proceed to meet <b>" + empName + "</b></p>"
                + "</td></tr></table>");
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.infoRow("Meeting with", empName));
            b.append(EmailTemplateBuilder.infoRow("Company", hostCompany));
            if (plantName != null && !plantName.isEmpty()) {
                b.append(EmailTemplateBuilder.infoRow("Location", plantName));
            }
            if (checkoutCode != null && !checkoutCode.isEmpty()) {
                b.append(EmailTemplateBuilder.otpSection("Use this code for Check-Out", checkoutCode));
            }
            helper.setText(EmailTemplateBuilder.wrap("Check-In Successful", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Email send failed", e);
        }
    }

    @Async
    public void sendRegeneratedOtpEmail(String to, String visitorName, String otp, String hostCompany) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Updated Check-Out OTP - " + hostCompany);
            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Your Check-Out OTP Has Been Updated"));
            b.append(EmailTemplateBuilder.text("A new check-out OTP has been generated for your visit to <b>" + hostCompany + "</b>."));
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.otpSection("Your New Check-Out OTP", otp));
            b.append(EmailTemplateBuilder.text("This OTP is valid for 2 minutes. Please use it at the reception to complete your check-out."));
            helper.setText(EmailTemplateBuilder.wrap("Check-Out OTP Updated", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send OTP regeneration email", e);
        }
    }

    public void sendCheckInEmailToEmp(String empEmail, String visitorName) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(empEmail);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Visitor Checked In - " + visitorName);

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Visitor Checked In"));
            b.append(EmailTemplateBuilder.text("<b>" + visitorName + "</b> has checked in at the reception."));
            b.append("<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background:#e6fff0;border-radius:12px;-webkit-border-radius:12px;margin:16px 0'><tr><td style='padding:16px;text-align:center'>"
                + "<p style='font-size:32px;margin:0 0 4px'>✅</p>"
                + "<p style='margin:0;font-size:14px;color:#389e0d;font-weight:600'>" + visitorName + " is now on the premises</p>"
                + "</td></tr></table>");
            b.append(EmailTemplateBuilder.text("Please proceed to the reception area to meet your visitor."));

            helper.setText(EmailTemplateBuilder.wrap("Visitor Checked In", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Email send failed", e);
        }
    }

    @Async
    public void sendThankYouEmail(String to, String visitorName, String empName, String companyName) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Thank You - Visit Confirmed - " + companyName);

            StringBuilder b = new StringBuilder();
            b.append("<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background:#e6fff0;border-radius:12px;-webkit-border-radius:12px;margin:16px 0'><tr><td style='padding:24px;text-align:center'>"
                + "<p style='font-size:48px;margin:0 0 8px'>🎉</p>"
                + "<h3 style='color:#389e0d;font-size:20px;margin:0 0 8px'>Thank You, " + visitorName + "!</h3>"
                + "<p style='margin:0;font-size:14px;color:#555'>Your visit has been confirmed. We look forward to seeing you.</p>"
                + "</td></tr></table>");
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.infoRow("Meeting with", empName));
            b.append(EmailTemplateBuilder.infoRow("Company", companyName));
            b.append(EmailTemplateBuilder.text("Please proceed to the reception upon arrival and present your invitation code."));

            helper.setText(EmailTemplateBuilder.wrap("Visit Confirmed", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Email send failed", e);
        }
    }

    @Async
    public void sendDeclinedEmailToVisitor(String to, String visitorName, String companyName) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Invitation Declined - " + companyName);

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Dear " + visitorName + ","));
            b.append(EmailTemplateBuilder.text("Your invitation to visit <b>" + companyName + "</b> has been declined."));
            b.append("<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background:#fff2f0;border-radius:12px;-webkit-border-radius:12px;margin:16px 0'><tr><td style='padding:24px;text-align:center'>"
                + "<p style='font-size:40px;margin:0 0 8px'>😔</p>"
                + "<p style='margin:0;font-size:14px;color:#cf1322'>We're sorry, but your visit request could not be approved at this time.</p>"
                + "</td></tr></table>");
            b.append(EmailTemplateBuilder.text("Please contact the host for more information."));

            helper.setText(EmailTemplateBuilder.wrap("Invitation Declined", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Email send failed", e);
        }
    }

    @Async
    public void sendApprovalRequestEmail(String empEmail, String visitorName, String companyName, String visitId) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(empEmail);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Portal Visit Update - " + visitorName);

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Portal Visit Update"));
            b.append(EmailTemplateBuilder.text("<b>" + visitorName + "</b> from <b>" + companyName + "</b> has requested to meet you."));
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.infoRow("Visitor", visitorName));
            b.append(EmailTemplateBuilder.infoRow("Company", companyName));
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.text("Please log in to the visitor portal to review and take action on this visit request."));

            helper.setText(EmailTemplateBuilder.wrap("Portal Visit Update", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Email send failed", e);
        }
    }

    @Async
    public void sendDirectCheckOutEmailToVisitor(String to, String visitorName, String hostCompany) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Thank You for Visiting " + hostCompany);

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Thank You, " + visitorName + "!"));
            b.append(EmailTemplateBuilder.text("Your visit to <b>" + hostCompany + "</b> has been completed."));
            b.append("<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background:#e6fff0;border-radius:12px;-webkit-border-radius:12px;margin:16px 0'><tr><td style='padding:24px;text-align:center'>"
                + "<p style='font-size:48px;margin:0 0 8px'>🎉</p>"
                + "<p style='margin:0;font-size:14px;color:#555'>We hope you had a great visit!</p>"
                + "</td></tr></table>");
            b.append(EmailTemplateBuilder.divider());

            helper.setText(EmailTemplateBuilder.wrap("Visit Complete", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Email send failed", e);
        }
    }

    public void sendCheckOutEmailToVisitor(String to, String visitorName, String checkoutCode, String hostCompany) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Thank You for Visiting " + hostCompany);

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Thank You, " + visitorName + "!"));
            b.append(EmailTemplateBuilder.text("Your visit to <b>" + hostCompany + "</b> has been completed."));
            b.append("<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background:#e6fff0;border-radius:12px;-webkit-border-radius:12px;margin:16px 0'><tr><td style='padding:24px;text-align:center'>"
                + "<p style='font-size:48px;margin:0 0 8px'>🎉</p>"
                + "<p style='margin:0;font-size:14px;color:#555'>We hope you had a great visit!</p>"
                + "</td></tr></table>");

            helper.setText(EmailTemplateBuilder.wrap("Visit Complete", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Email send failed", e);
        }
    }

    @Async
    public void sendCheckOutEmailToEmp(String empEmail, String visitorName) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(empEmail);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Visitor Checked Out - " + visitorName);

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Visitor Checked Out"));
            b.append(EmailTemplateBuilder.text("<b>" + visitorName + "</b> has checked out from the premises."));
            b.append("<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background:#fff7e6;border-radius:12px;-webkit-border-radius:12px;margin:16px 0'><tr><td style='padding:16px;text-align:center'>"
                + "<p style='font-size:32px;margin:0 0 4px'>👋</p>"
                + "<p style='margin:0;font-size:14px;color:#d46b08;font-weight:600'>" + visitorName + " has departed</p>"
                + "</td></tr></table>");

            helper.setText(EmailTemplateBuilder.wrap("Visitor Checked Out", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Email send failed", e);
        }
    }

    @Async
    public void sendCredentialsEmail(String to, String name, String username, String tempPassword) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Your Visitor Connect Account Credentials");

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Welcome, " + name + "!"));
            b.append(EmailTemplateBuilder.text("Your account has been created on <b>Visitor Connect</b>."));
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.infoRow("Username", username));
            b.append(EmailTemplateBuilder.otpSection("Your Temporary Password", tempPassword));
            b.append(EmailTemplateBuilder.text("You must change this password on your first login for security purposes."));
            b.append(EmailTemplateBuilder.text("Please log in at the Visitor Connect portal and follow the prompts to set a new password."));

            helper.setText(EmailTemplateBuilder.wrap("Account Credentials", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send credentials email to {}", to, e);
        }
    }

    @Async
    public void sendTempPasswordEmail(String to, String name, String username, String tempPassword) {
        try {
            JavaMailSenderImpl sender = createMailSender();
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("Password Reset - Temporary Password");

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("Hello " + name + ","));
            b.append(EmailTemplateBuilder.text("A password reset has been requested for your <b>Visitor Connect</b> account."));
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.infoRow("Username", username));
            b.append(EmailTemplateBuilder.otpSection("Your Temporary Password", tempPassword));
            b.append(EmailTemplateBuilder.text("Use this temporary password to log in. You will be prompted to set a new password after logging in."));
            b.append(EmailTemplateBuilder.text("For security reasons, this temporary password will expire after first use."));

            helper.setText(EmailTemplateBuilder.wrap("Password Reset", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send temp password email to {}", to, e);
        }
    }

    public void sendTestEmail(String to) {
        JavaMailSenderImpl sender = createMailSender();
        try {
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            if (sender.getUsername() != null) helper.setFrom(sender.getUsername());
            helper.setSubject("SMTP Test - Visitor Connect");

            StringBuilder b = new StringBuilder();
            b.append(EmailTemplateBuilder.heading("SMTP Configuration Test"));
            b.append(EmailTemplateBuilder.text("This is a test email from the <b>Visitor Connect</b>."));
            b.append("<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background:#e6f0ff;border-radius:12px;-webkit-border-radius:12px;margin:16px 0'><tr><td style='padding:16px;text-align:center'>"
                + "<p style='font-size:32px;margin:0 0 4px'>✅</p>"
                + "<p style='margin:0;font-size:14px;color:#1890ff;font-weight:600'>Your SMTP settings are working correctly!</p>"
                + "</td></tr></table>");
            b.append(EmailTemplateBuilder.text("If you received this, your email configuration is properly set up."));
            b.append(EmailTemplateBuilder.divider());
            b.append(EmailTemplateBuilder.infoRow("Server", sender.getHost()));
            b.append(EmailTemplateBuilder.infoRow("Port", String.valueOf(sender.getPort())));
            b.append(EmailTemplateBuilder.infoRow("Username", sender.getUsername()));

            helper.setText(EmailTemplateBuilder.wrap("SMTP Test", b.toString()), true);
            sender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Email send failed: " + e.getMessage());
        }
    }
}
