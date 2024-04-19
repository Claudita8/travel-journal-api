package travel.journal.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${Email_Username}")
    private String Email_Username;
    @Value("${Email_Password}")
    private String Email_Password;
    @Value("${Email_Host}")
    private String Email_Host;
    @Value("${Email_Port}")
    private int Email_Port;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(Email_Username);
        mailSender.setPassword(Email_Password);
        mailSender.setPort(Email_Port);
        mailSender.setHost(Email_Host);
        mailSender.setDefaultEncoding("UTF-8");
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.checkserveridentity", "true");
        return mailSender;
    }
}