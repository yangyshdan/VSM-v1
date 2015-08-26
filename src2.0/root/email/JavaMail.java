package root.email;

import java.util.Properties;  
import javax.mail.Message;  
import javax.mail.Session;  
import javax.mail.Transport;  
import javax.mail.internet.InternetAddress;  
import javax.mail.internet.MimeMessage;  

/**
 * 邮件发送类
 * @Name JavaMail
 * @Author gugu
 * @Date 2013-11-6上午11:04:18
 * @Description TODO
 */
public class JavaMail {  
  
    // 设置服务器  
    private static String KEY_SMTP = "mail.smtp.host";  
    private static String KEY_PORT= "mail.smtp.port";  
    // 服务器验证  
    private static String KEY_PROPS = "mail.smtp.auth";  
    // 建立会话  
    private MimeMessage message;  
    private Session s;  
    
    public void initProps(String smtp,String port){
    	 Properties props = System.getProperties();  
         props.setProperty(KEY_SMTP, smtp);  
   //      props.setProperty(KEY_PORT, port);  
         props.put(KEY_PROPS, true);  
         s = Session.getInstance(props);  
         message = new MimeMessage(s);  
    }
  
    /** 
     * 发送邮件 
     *  
     * @param headName 
     *            邮件头文件名 
     * @param sendHtml 
     *            邮件内容 
     * @param receiveUser 
     *            收件人地址 
     */  
    public void doSendHtmlEmail(String smtp,String sendUser,String sendPwd,String headName, String sendHtml,  
            String receiveUser) {  
        try {  
            // 发件人  
            InternetAddress from = new InternetAddress(sendUser);  
            message.setFrom(from);  
            // 收件人  
            InternetAddress to = new InternetAddress(receiveUser);  
            message.setRecipient(Message.RecipientType.TO, to);  
            // 邮件标题  
            message.setSubject(headName);  
            String content = sendHtml.toString();  
            // 邮件内容,也可以使纯文本"text/plain"  
            message.setContent(content, "text/html;charset=GBK");  
            message.saveChanges();  
            Transport transport = s.getTransport("smtp");  
            // smtp验证，就是你用来发邮件的邮箱用户名密码  
            transport.connect(smtp, sendUser, sendPwd);  
            // 发送  
            transport.sendMessage(message, message.getAllRecipients());  
            transport.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
    }  
  
}  
