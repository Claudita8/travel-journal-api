package travel.journal.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587); // Portul 587 este cel mai comun pentru Gmail
//        mailSender.setUsername("picuandreia2001v2@gmail.com");
//        mailSender.setPassword("nbcjlnfttwtchbkl");
//        mailSender.setUsername("testlicenta24@gmail.com");
//        mailSender.setPassword("odsdiclpntjymznj");
//        mailSender.setDefaultEncoding("UTF-8");
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.smtp.starttls.enable", "true");
//        return mailSender;
//    }
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("test@stancuf.ro");
        mailSender.setPassword("Stancu2002");
        mailSender.setPort(465);
        mailSender.setHost("mail.stancuf.ro");
        mailSender.setDefaultEncoding("UTF-8");
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.checkserveridentity", "true");
        return mailSender;
    }
}