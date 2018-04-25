package oracle.wallet.maintenance;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileInputStream;
import java.io.InputStream;

public class SendEmail{
    private String to, from, cc, host;
    private Session session = null;

    public SendEmail(String configFile){
        Properties prop = new Properties();
        try  {
            InputStream in = new FileInputStream(configFile);
            prop.load(in);
        } catch (Exception e){
            e.printStackTrace();
        }

        this.to = prop.getProperty("mail.to");
        this.from = prop.getProperty("mail.from");
        this.cc = prop.getProperty("mail.cc");
        this.host = prop.getProperty("mail.host");

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.starttls.enable", "true");

        this.session = session.getDefaultInstance(properties);
    }

    public void send(String subject, String text){
        try {
            MimeMessage message = new MimeMessage(this.session);
            message.setFrom(new InternetAddress(this.from));
            for (int i = 0; i < this.to.split(",").length; i++){
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(this.to.split(",")[i]));
            }
            if (this.cc != null && !(this.cc.isEmpty())){
                for (int i = 0; i < this.cc.split(",").length; i++){
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(this.cc.split(",")[i]));
                }
            }
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch(MessagingException mex){
            mex.printStackTrace();
        }
    }
}