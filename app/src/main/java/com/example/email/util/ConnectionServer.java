package com.example.email.util;

import com.example.email.bean.EmailBean;

import org.litepal.crud.DataSupport;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class ConnectionServer {

    public static Message[] connection(int emailId){

        Message[] messages;
        final EmailBean email = DataSupport.find(EmailBean.class,emailId);

        Properties prop = new Properties();
        prop.put("mail.store.protocol", email.getProtocol());
        prop.put("mail.imap.host", email.getHost());
        prop.put("mail.imap.ssl.enable", true);

        Session session = Session.getInstance(prop);
        session.setDebug(true);
        Store store = null;
        Folder folder = null;
        try {

            store = session.getStore(email.getProtocol());
            store.connect(email.getAddress(), email.getPassword());

            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            messages = folder.getMessages();

        } catch (javax.mail.NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (folder != null) {
                    folder.close();
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }
}
