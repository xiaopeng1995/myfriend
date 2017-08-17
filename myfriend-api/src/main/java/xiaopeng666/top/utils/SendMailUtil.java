package xiaopeng666.top.utils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendMailUtil {

    private static final Logger log = LoggerFactory.getLogger(SendMailUtil.class);
    static private String mailServer = null;
    static private String mailName = null;
    static private String mailPwd = null;
    static private String mailFrom = null;//邮件发送人邮件地址
    static private String mailAliasname = null;//邮件发送人名称
    private static ExecutorService executor;

    static {
        loads();
    }

    static {
        executor = Executors.newFixedThreadPool(50);
    }

    synchronized static public void loads() {
        if (mailServer == null || mailName == null || mailPwd == null) {
            PropertiesConfiguration config = null;
            try {
                config = new PropertiesConfiguration("src/dist/config/serverConf.properties");
                //编译前

            } catch (Exception e) {
                try {
                    config = new PropertiesConfiguration("config/serverConf.properties");
                    //编译后路径
                } catch (Exception ee) {
                    ee.printStackTrace();
                    log.error("不能读取属性文件. " + "请确保properties在CLASSPATH指定的路径中");
                }

            }
            if (config != null) {
                mailServer = config.getString("mailServer");
                mailName = config.getString("mailName");
                mailPwd = config.getString("mailPwd");
                mailAliasname = config.getString("mailAliasname");
                mailFrom = config.getString("mailFrom");
            }
        }
    }

    /**
     * 功能:发送邮件
     *
     * @param mialTitle   邮件主题
     * @param attachPath  附件地址
     * @param attachName  附件显示名称
     * @param toEmail     收件人
     * @param mailContent 邮件内容
     */
    public static void sendEmail(final String mialTitle, final String attachPath, final String attachName, final String toEmail,
                                 final String mailContent) throws MalformedURLException, UnsupportedEncodingException, EmailException {

        executor.execute(new Runnable() {
            public void run() {
                EmailAttachment attachment = null;
                if (attachPath != null) {
                    attachment = new EmailAttachment();
                    try {
                        attachment.setPath(attachPath);
                        attachment.setName(MimeUtility.encodeText(attachName));
                        attachment.setDisposition(EmailAttachment.ATTACHMENT);
                        // attachment.setDescription("Picture of John");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        log.info(e.getMessage());
                    }
                }

                HtmlEmail email = new HtmlEmail();
                email.setHostName(mailServer);
                email.setAuthentication(mailName, mailPwd);
                email.setCharset("UTF-8");
                try {
                    email.setFrom(mailFrom, mailAliasname);// mailFrom发件人,mailLame发件人的别名
                    // 设置发件人
                    email.addTo(toEmail);// 设置收件人
                    email.setSubject(mialTitle);// 设置主题
                    email.setHtmlMsg((mailContent == null || "".equals(mailContent)) ? "" : mailContent);// 可以发送html
                    if (attachPath != null) {
                        email.attach(attachment);
                    }
                    log.info("mailServer:" + mailServer + " mailTo: " + toEmail);
                    log.info("邮件主题：" + mialTitle);
                    log.info("发送成功！");
                    email.send();
                } catch (EmailException e) {
                    e.printStackTrace();
                    log.info(e.getMessage());
                }
            }
        });
    }

    /**
     * 功能:发送邮件通用
     *
     * @param mialTitle   邮件主题
     * @param toEmail     收件人
     * @param mailContent 邮件内容
     */
    public static void sendEmail(final String mialTitle, final String toEmail, final String mailContent) {

        executor.execute(new Runnable() {
            public void run() {
                HtmlEmail email = new HtmlEmail();
                email.setHostName(mailServer);
                email.setAuthentication(mailName, mailPwd);
                email.setCharset("UTF-8");
                try {
                    email.setFrom(mailFrom, mailAliasname);// mailFrom发件人,mailLame发件人的别名
                    // 设置发件人
                    email.addTo(toEmail);// 设置收件人
                    email.setSubject(mialTitle);// 设置主题
                    email.setHtmlMsg((mailContent == null || "".equals(mailContent)) ? "" : mailContent);// 可以发送html
                    log.info("mailServer:" + mailServer + " mailTo: " + toEmail);
                    log.info("Mail Title：" + mialTitle);
                    email.send();
                } catch (Exception e) {
                    log.info("邮件发送出现异常:{}", e);
                }
            }
        });
    }

    public static void sendEmail(final String mialTitle, final String toEmail, final String mailContent, Date date) {

        executor.execute(new Runnable() {
            public void run() {
                HtmlEmail email = new HtmlEmail();
                email.setHostName(mailServer);
                email.setAuthentication(mailName, mailPwd);
                email.setCharset("UTF-8");
                try {
                    email.setFrom(mailFrom, mailAliasname);// mailFrom发件人,mailLame发件人的别名
                    // 设置发件人
                    email.addTo(toEmail);// 设置收件人
                    email.setSubject(mialTitle);// 设置主题
                    email.setHtmlMsg((mailContent == null || "".equals(mailContent)) ? "" : mailContent);// 可以发送html
                    log.info("mailServer:" + mailServer + " mailTo: " + toEmail);
                    log.info("Mail Title：" + mialTitle);
                    email.send();
                } catch (Exception e) {
                    log.info("邮件发送出现异常:{}", e);
                }
            }
        });
    }

    /**
     * 功能:发送邮件通用
     *
     * @param mialTitle   邮件主题
     * @param toEmail     收件人
     * @param mailContent 邮件内容
     * @param type        类型
     */
    public static void sendEmail(final String mialTitle, final String toEmail, final String mailContent, int type) {

        executor.execute(new Runnable() {
            public void run() {
                HtmlEmail email = new HtmlEmail();
                email.setHostName(mailServer);
                email.setAuthentication(mailName, mailPwd);
                email.setCharset("UTF-8");
                try {
                    email.setFrom(mailFrom, mailAliasname);// mailFrom发件人,mailLame发件人的别名
                    // 设置发件人
                    email.addTo(toEmail);// 设置收件人
                    email.setSubject(mialTitle);// 设置主题
                    email.setHtmlMsg((mailContent == null || "".equals(mailContent)) ? "" : mailContent);// 可以发送html
                    log.info("mailServer:" + mailServer + " mailTo: " + toEmail);
                    log.info("邮件主题：" + mialTitle);
                    email.send();
                } catch (Exception e) {
                    log.info("邮件发送出现异常:{}", e);
                }
            }
        });
    }


}