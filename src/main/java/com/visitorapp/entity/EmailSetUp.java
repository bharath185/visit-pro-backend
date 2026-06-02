package com.visitorapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "EmailSetUp")
public class EmailSetUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "CompId")
    private Integer compId;

    @Column(name = "EmailId")
    private String emailId;

    @Column(name = "SMTPServer")
    private String smtpServer;

    @Column(name = "SMTPPort")
    private String smtpPort;

    @Column(name = "SMTPMailId")
    private String smtpMailId;

    @Column(name = "SMTPPassword")
    private String smtpPassword;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCompId() { return compId; }
    public void setCompId(Integer compId) { this.compId = compId; }
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public String getSmtpServer() { return smtpServer; }
    public void setSmtpServer(String smtpServer) { this.smtpServer = smtpServer; }
    public String getSmtpPort() { return smtpPort; }
    public void setSmtpPort(String smtpPort) { this.smtpPort = smtpPort; }
    public String getSmtpMailId() { return smtpMailId; }
    public void setSmtpMailId(String smtpMailId) { this.smtpMailId = smtpMailId; }
    public String getSmtpPassword() { return smtpPassword; }
    public void setSmtpPassword(String smtpPassword) { this.smtpPassword = smtpPassword; }
}
