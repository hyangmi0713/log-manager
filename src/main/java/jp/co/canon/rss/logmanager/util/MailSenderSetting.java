package jp.co.canon.rss.logmanager.util;

import jp.co.canon.rss.logmanager.vo.SiteVo;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Optional;
import java.util.Properties;

public class MailSenderSetting {
    public JavaMailSender getMailSenderSetting(SiteVo siteVo) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(siteVo.getEmailAddress());
        mailSender.setPort(siteVo.getEmailPort());

        mailSender.setUsername(siteVo.getEmailUserName());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.connectiontimeout", "100000");
        props.put("mail.smtp.timeout", "100000");

        return mailSender;
    }
}
