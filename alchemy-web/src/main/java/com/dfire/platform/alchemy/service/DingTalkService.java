package com.dfire.platform.alchemy.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DingTalkService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${alchemy.dingtalk.webhook}")
    private String webhook;

    /**
     * 发送markdown类型消息
     * @param title       首屏会话透出的展示内容
     * @param text        markdown格式的消息
     */
    public void sendMessage(String title,String text){
       sendMessage(title, text, null);
    }

    /**
     * 发送markdown类型消息
     * @param title       首屏会话透出的展示内容
     * @param text        markdown格式的消息
     * @param atMobiles   被@人的手机号(在text内容里要有@手机号)
     */
    public void sendMessage(String title,String text,String[] atMobiles){
        MarkdownMessage markdownMessage=new MarkdownMessage(title,text,atMobiles);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MarkdownMessage> entity = new HttpEntity<MarkdownMessage>(markdownMessage, headers);
        restTemplate.postForEntity(webhook, entity,Void.class);
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public class MarkdownMessage {

        private String msgtype="markdown";
        private Markdown markdown;
        private At at;

        public MarkdownMessage(String title,String text,String[] atMobiles){
            this.markdown=new Markdown(title,text);
            this.at=new At(atMobiles);
        }

        public String getMsgtype() {
            return msgtype;
        }

        public void setMsgtype(String msgtype) {
            this.msgtype = msgtype;
        }

        public Markdown getMarkdown() {
            return markdown;
        }

        public void setMarkdown(Markdown markdown) {
            this.markdown = markdown;
        }

        public At getAt() {
            return at;
        }

        public void setAt(At at) {
            this.at = at;
        }
    }

    public static class Markdown {

        private String title;

        private String text;

        public Markdown(String title,String text){
            this.title=title;
            this.text=text;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

    public static class At {

        private String[] atMobiles;
        private boolean isAtAll;

        public At(String[] atMobiles){
            if(atMobiles!=null&&atMobiles.length>0){
                this.atMobiles=atMobiles;
                this.isAtAll=false;
            }else{
                this.atMobiles=new String[0];
                this.isAtAll=true;
            }
        }

        public String[] getAtMobiles() {
            return atMobiles;
        }

        public void setAtMobiles(String[] atMobiles) {
            this.atMobiles = atMobiles;
        }

        public boolean isAtAll() {
            return isAtAll;
        }

        public void setAtAll(boolean atAll) {
            isAtAll = atAll;
        }

    }
}
