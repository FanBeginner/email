package com.example.email.main;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.email.bean.Attachment;
import com.example.email.util.ConnectionServer;
import com.example.email.util.DecodeOneEmail;
import com.example.email.bean.EmailBean;
import com.example.email.bean.MessageBean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.event.StoreEvent;
import javax.mail.event.StoreListener;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

public class MainModel implements IMainModel {

    private IMainPresenter iMainPresenter;
    //private int emailId;
    private Activity activity;

    MainModel(IMainPresenter iMainPresenter){
        this.iMainPresenter = iMainPresenter;
    }

    @Override
    public void getMessageList(final int emailId, final Activity activity) {
        //this.emailId = emailId;
        this.activity = activity;

        List<MessageBean> messageBeans = DataSupport.where("emailId = ? and isLocal = ?",
                String.valueOf(emailId), "0").order("id desc").find(MessageBean.class);
        if (messageBeans.size() > 0){         //说明本地有邮件
            List<MessageBean> notDeleteList = DataSupport.where("emailId = ? and isLocal = ? and isDelete = ?",
                    String.valueOf(emailId), "0","0").order("id desc").find(MessageBean.class);   //查询本地未被删除的邮件
            if (notDeleteList.size() == 0){       //如果邮件都被删除，则弹出消息提示，没有邮件
                Toast.makeText(activity,"没有邮件",Toast.LENGTH_SHORT).show();
            }
            iMainPresenter.getListSuccess(notDeleteList);
        }else{
            getListFromServer();
        }
    }

    @Override
    public void getListFromServer() {
        EmailBean email = getEmail(getEmailId());

        final String user = email.getAddress();
        final String password = email.getPassword();

        Properties prop = new Properties();
        if (email.getProtocol().equals("imap")){
            prop.put("mail.store.protocol", email.getProtocol());
            prop.put("mail.imap.host", email.getHost());
            prop.put("mail.imap.ssl.enable", true);
        }else if (email.getProtocol().equals("pop3")){
            prop.put("mail.store.protocol", email.getProtocol());
            prop.put("mail.pop3.host", email.getHost());
            prop.put("mail.pop3.ssl.enable", true);
        }

        Session session = Session.getInstance(prop);
        session.setDebug(true);
        try {
            final Store store =  session.getStore(email.getProtocol());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Folder folder = null;
                    try {
                        store.connect(user, password);
                        folder = store.getFolder("INBOX");
                        folder.open(Folder.READ_ONLY);

                        Message[] messages = folder.getMessages();

                        final List<MessageBean> list = new ArrayList<>();
                        if (messages.length > 0) {
                            for (int i=0;i<messages.length;i++){
                                DecodeOneEmail email = new DecodeOneEmail((MimeMessage) messages[i],activity,i,getEmailId());
                                MessageBean bean = email.getMessageBean();
                                list.add(0,bean);
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iMainPresenter.getListSuccess(list);
                                }
                            });
                        }
                    } catch (MessagingException e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iMainPresenter.getListFail();
                            }
                        });
                        e.printStackTrace();
                    } catch (Exception e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iMainPresenter.getListFail();
                            }
                        });
                        e.printStackTrace();
                    }finally {
                        try {
                            if (folder != null) {
                                folder.close();
                            }
                            store.close();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (javax.mail.NoSuchProviderException e) {
            iMainPresenter.getListFail();
            e.printStackTrace();
        } catch (Exception e) {
            iMainPresenter.getListFail();
            e.printStackTrace();
        }
    }

    @Override
    public void refreshData(final int emailId) {

        EmailBean email = getEmail(emailId);
        final String user = email.getAddress();
        final String password = email.getPassword();

        Properties prop = new Properties();
        if (email.getProtocol().equals("imap")){
            prop.put("mail.store.protocol", email.getProtocol());
            prop.put("mail.imap.host", email.getHost());
            prop.put("mail.imap.ssl.enable", true);
        }else if (email.getProtocol().equals("pop3")){
            prop.put("mail.store.protocol", email.getProtocol());
            prop.put("mail.pop3.host", email.getHost());
            prop.put("mail.pop3.ssl.enable", true);
        }

        Session session = Session.getInstance(prop);
        session.setDebug(true);
        try {
            final Store store =  session.getStore(email.getProtocol());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Folder folder = null;
                    try {
                        store.connect(user, password);
                        folder = store.getFolder("INBOX");
                        folder.open(Folder.READ_ONLY);

                        final Message[] messages = folder.getMessages();
                        final List<MessageBean> list = new ArrayList<>();
                        if (messages.length > 0) {
                            DataSupport.deleteAll(MessageBean.class,
                                    "isLocal = ? and emailId = ?","0",String.valueOf(emailId));
                            DataSupport.deleteAll(Attachment.class,
                                    "isLocal = ? and emailId = ?","0",String.valueOf(emailId));
                            for (int i=0;i<messages.length;i++){
                                DecodeOneEmail email = new DecodeOneEmail((MimeMessage) messages[i],activity,i,emailId);
                                MessageBean bean = email.getMessageBean();
                                list.add(0,bean);
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iMainPresenter.refreshSuccess(list);
                                }
                            });
                        }
                    } catch (MessagingException e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iMainPresenter.refreshFail();
                            }
                        });
                        e.printStackTrace();
                    } catch (Exception e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iMainPresenter.refreshFail();
                            }
                        });
                        e.printStackTrace();
                    }finally {
                        try {
                            if (folder != null) {
                                folder.close();
                            }
                            store.close();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (javax.mail.NoSuchProviderException e) {
            iMainPresenter.refreshFail();
            e.printStackTrace();
        } catch (Exception e) {
            iMainPresenter.refreshFail();
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEmail(List<Integer> list) {
        EmailBean email = DataSupport.find(EmailBean.class,getEmailId());

        Properties prop = new Properties();
        if (email.getProtocol().equals("imap")){
            prop.put("mail.store.protocol", email.getProtocol());
            prop.put("mail.imap.host", email.getHost());
            prop.put("mail.imap.ssl.enable", true);
        }else if (email.getProtocol().equals("pop3")){
            prop.put("mail.store.protocol", email.getProtocol());
            prop.put("mail.pop3.host", email.getHost());
            prop.put("mail.pop3.ssl.enable", true);
        }

        Session session = Session.getInstance(prop);
        session.setDebug(true);
        Store store = null;
        Folder folder = null;
        try {

            store = session.getStore(email.getProtocol());
            store.connect(email.getAddress(), email.getPassword());

            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages();
            if (messages.length > 0){
                for (Integer integer:list){
                    Message message = messages[integer];
                    message.setFlag(Flags.Flag.DELETED,true);
                }
            }

        } catch (javax.mail.NoSuchProviderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (folder != null) {
                    folder.close(true);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateSubscript() {
        EmailBean email = DataSupport.find(EmailBean.class,getEmailId());

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

            Message[] messages = folder.getMessages();
            if (messages.length > 0){
                List<MessageBean> messageBeans = DataSupport.where("emailId = ? and isLocal = ?",
                        String.valueOf(getEmailId()), "0").find(MessageBean.class);
                if (messages.length == messageBeans.size()){      //数量相等，说明没有新邮件
                    for (int i=0;i<messages.length;i++){
                        MessageBean messageBean = messageBeans.get(i);
                        if (messageBean.isAttachment()){         //判断是否有附件
                            List<Attachment> attachmentList = DataSupport.findAll(Attachment.class,messageBean.getOtherId());
                            for (Attachment attachment:attachmentList){
                                attachment.setMessageId(i);
                                attachment.update(attachment.getId());       //更新附件与之相连的messageId
                            }
                        }
                        messageBean.setOtherId(i);
                        messageBean.update(messageBean.getId());
                    }
                }else {
                    //说明有新邮件到来，你真是不凑巧，我我我我我。。。。。。卧槽
                    //默认倒霉呗，这时不处理新邮件，应该不会出bug
                }
            }

        } catch (javax.mail.NoSuchProviderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (folder != null) {
                    folder.close(true);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private int getEmailId(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getInt("email_id",0);
    }

    private EmailBean getEmail(int emailId){
        return DataSupport.find(EmailBean.class,emailId);
    }

}
