package com.imputation.jobs.email.service;

/**
 * @author fanshupeng
 * @create 2022/5/6 16:23
 */
public interface IMailService {
    /**
     * 发送邮件
     * @param receiver 邮件收件人
     * @param subject 邮件主题
     * @param verCode 邮件验证码
     */
    void sendEmailVerCode(String receiver, String subject, String verCode);

    /**
     * 发送HTML邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    void sendHtmlMail(String to, String subject, String content);

    /**
     * 发送结果提醒邮件
     * @param to
     * @param subject
     * @param content
     */
    void sendResultMail(String to, String subject, String content);
    /**
     * 发送带附件的邮件
     *
     * @param to       收件人
     * @param subject  主题
     * @param content  内容
     * @param filePath 附件
     */
    void sendAttachmentsMail(String to, String subject, String content, String filePath);
}
