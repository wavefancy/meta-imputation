package com.imputation.jobs.email.service.impl;

import com.imputation.jobs.email.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author fanshupeng
 * @create 2022/5/6 16:24
 */
@Slf4j
@Service
public class MailServiceImpl implements IMailService {

    @Autowired
    JavaMailSenderImpl mailSender;

    @Autowired
    TemplateEngine templateEngine;
    /**
     * 从配置文件中获取发件人
     */
    @Value("${spring.mail.username}")
    private String sender;
    /**
     * 访问地址
     */
    @Value("${system.service.path}")
    private String systemServicePath;

    /**
     * 邮件发送
     * @param receiver 收件人
     * @param subject 主题
     * @param verCode 验证码
     * @throws MailSendException 邮件发送错误
     */
    @Async
    public void sendEmailVerCode(String receiver, String subject, String verCode) throws MailSendException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);	//设置邮件标题
        message.setText("尊敬的用户,您好:\n"
                + "\n请点击下方的“邮箱激活”，进行注册激活:\n<a href=\'"+verCode+"\'>邮箱激活</a>\n本次激活链接30分钟内有效，请及时激活。\n"
                + "\n如非本人操作，请忽略该邮件。\n(这是一封自动发送的邮件，请不要直接回复）");	//设置邮件正文
        message.setTo(receiver);	//设置收件人
        message.setFrom(sender);	//设置发件人
        mailSender.send(message);	//发送邮件
    }

    /**
     * html邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        //获取MimeMessage对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true,"UTF-8");
            //邮件发送人
            messageHelper.setFrom(sender);
            //邮件接收人
            messageHelper.setTo(to);
            //邮件主题
            message.setSubject(subject);
            messageHelper.setText("尊敬的用户,您好:<br>"
                    + "<br>请点击下方的“邮箱激活”，进行注册激活:<br><a href=\'"+systemServicePath+"/imputation/job/authActive?msg="+content+"\'>邮箱激活</a><br>本次激活链接30分钟内有效，请及时激活。<br>"
                    + "<br>如非本人操作，请忽略该邮件。<br>(这是一封自动发送的邮件，请不要直接回复）", true);
            //发送
            mailSender.send(message);
            //日志信息
            log.info("邮件已经发送。");
        } catch (Exception e) {
            log.error("发送邮件时发生异常！", e);
        }
    }
    /**
     * 运行结果邮件
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    @Override
    public void sendResultMail(String to, String subject, String content) {
        log.info(content+"，发送邮件开始，\n收件人="+to);
        //获取MimeMessage对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true,"UTF-8");
            //邮件发送人
            messageHelper.setFrom(sender);
            //邮件接收人
            messageHelper.setTo(to);
            //邮件主题
            message.setSubject(subject);
            messageHelper.setText(content, true);
            //发送
            mailSender.send(message);
            //日志信息
            log.info(content+",邮件已经发送。");
        } catch (Exception e) {
            log.error("发送邮件时发生异常！", e);
        }
    }
    /**
     * 带附件的邮件
     *
     * @param to       收件人
     * @param subject  主题
     * @param content  内容
     * @param filePath 附件
     */
    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);
            mailSender.send(message);
            //日志信息
            log.info("邮件已经发送。");
        } catch (Exception e) {
            log.error("发送邮件时发生异常！", e);
        }


    }
}
