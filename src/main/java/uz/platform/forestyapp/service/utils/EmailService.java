package uz.platform.forestyapp.service.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Transactional
public class EmailService {
    @Autowired
    JavaMailSender javaMailSender;

    public void sendEmailCodeFirstLogIn(String sendingEmail, String emailCode, String lastName, String firstName,String username,String password) throws MessagingException {

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
            mimeMessageHelper
                    = new MimeMessageHelper(mailMessage, true);
            mimeMessageHelper.setFrom("Foresty");
            mimeMessageHelper.setTo(sendingEmail);
            mimeMessageHelper.setSubject("[Foresty] Iltimos emailingizni tasdiqlang");
            mimeMessageHelper.setText("<p>Assalomu alaykum "+firstName+" "+lastName+"!</p><br/>" +
                    "<p>Foresty platformasiga kirish uchun ushbu jo'natilgan kod orqali o'z emailingizni tasdiqlashingiz kerak.</p>" +
                    "<p>Ushbu yuborilgan email kod 5 daqiqadan keyin yaroqsiz bo'ladi.</p>" +
                    "<h3>"+emailCode+"</h3><br/>"  +"<p>Login: <h5>"+username+"</h5></p><br/>"+"<p>Parol: <h5>"+password+"</h5></p><br/>"+
                    "<a style='padding:13px; border-radius:8px; color:#fff;background-color:#00a652;text-decoration:none;font-size:16px' href='http://localhost:8080/api/auth/verifyEmail?emailCode="+emailCode+"&email="+sendingEmail+"'>Tasdiqlash</a><br/>",true);
            javaMailSender.send(mailMessage);

    }

    public void sendEmailCode(String sendingEmail, String emailCode, String lastName, String firstName)throws MessagingException {

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
            mimeMessageHelper
                    = new MimeMessageHelper(mailMessage, true);
            mimeMessageHelper.setFrom("Foresty");
            mimeMessageHelper.setTo(sendingEmail);
            mimeMessageHelper.setSubject("[Foresty] Iltimos emailingizni tasdiqlang");
            mimeMessageHelper.setText("<p>Assalomu alaykum "+firstName+" "+lastName+"!</p><br/>" +
                    "<p>Foresty platformasiga kirish uchun ushbu jo'natilgan kod orqali o'z emailingizni tasdiqlashingiz kerak.</p>" +
                    "<p>Ushbu yuborilgan email kod 5 daqiqadan keyin yaroqsiz bo'ladi.</p>" +
                    "<h3>"+emailCode+"</h3><br/>" +
                    "<a style='padding:13px; border-radius:8px; color:#fff;background-color:#00a652;text-decoration:none;font-size:16px' href='http://localhost:8080/api/auth/verifyEmail?emailCode="+emailCode+"&email="+sendingEmail+"'>Tasdiqlash</a><br/>",true);
            javaMailSender.send(mailMessage);

    }

    public void sendEmailUsernameChange(String sendingEmail, String lastName, String firstName,String username)throws MessagingException {

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        mimeMessageHelper
                = new MimeMessageHelper(mailMessage, true);
        mimeMessageHelper.setFrom("Foresty");
        mimeMessageHelper.setTo(sendingEmail);
        mimeMessageHelper.setSubject("[Foresty] Username o'zgartirildi!");
        mimeMessageHelper.setText("<p>Assalomu alaykum "+firstName+" "+lastName+"!</p><br/>" +
                "<p>Foresty platformasidagi ma'lumotlaringiz o'zgartirildi!.</p>" +
                "<p>Username: <h3>"+username+"</h3></p>" +
                "<br/>"
                ,true);
        javaMailSender.send(mailMessage);

    }

    public void sendEmailPasswordChange(String sendingEmail, String lastName, String firstName,String password)throws MessagingException {

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        mimeMessageHelper
                = new MimeMessageHelper(mailMessage, true);
        mimeMessageHelper.setFrom("Foresty");
        mimeMessageHelper.setTo(sendingEmail);
        mimeMessageHelper.setSubject("[Foresty] Parol o'zgartirildi!");
        mimeMessageHelper.setText("<p>Assalomu alaykum "+firstName+" "+lastName+"!</p><br/>" +
                        "<p>Foresty platformasidagi ma'lumotlaringiz o'zgartirildi!.</p>" +
                        "<p>Parol: <h3>"+password+"</h3></p>" +
                        "<br/>"
                ,true);
        javaMailSender.send(mailMessage);

    }

    public void sendEmailCodeToUser(String sendingEmail, String emailCode, String lastName, String firstName) throws MessagingException{

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
            mimeMessageHelper
                    = new MimeMessageHelper(mailMessage, true);
            mimeMessageHelper.setFrom("Foresty");
            mimeMessageHelper.setTo(sendingEmail);
            mimeMessageHelper.setSubject("[Foresty] Iltimos emailingizni tasdiqlang");
            mimeMessageHelper.setText("<p>Assalomu alaykum "+firstName+" "+lastName+"!</p><br/>" +
                    "<p>Foresty platformasiga kirish uchun ushbu jo'natilgan kod orqali o'z emailingizni tasdiqlashingiz kerak.</p>" +
                    "<p>Ushbu yuborilgan email kod 5 daqiqadan keyin yaroqsiz bo'ladi.</p>" +
                    "<h3>"+emailCode+"</h3><br/>"+
                    "<a style='padding:13px; border-radius:8px; color:#fff;background-color:#00a652;text-decoration:none;font-size:16px' href='http://localhost:8080/api/auth/verifyEmailUser?emailCode="+emailCode+"&email="+sendingEmail+"'>Tasdiqlash</a><br/>",true);
            javaMailSender.send(mailMessage);

    }



    public String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}
