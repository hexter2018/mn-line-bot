package com.aten.mn.line.bot;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@SpringBootApplication
@LineMessageHandler
public class Application extends SpringBootServletInitializer {

	@Autowired
	private LineMessagingClient lineMessagingClient;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Application.class);
	}

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

	}

	@EventMapping
	public void handleTextEvent(MessageEvent<TextMessageContent> messageEvent){
		try {
			String pesan = messageEvent.getMessage().getText().toLowerCase();

			if(pesan.startsWith("/")) {
				String coin = pesan.startsWith("/p ")?pesan.split(" ")[1]:pesan.substring(1, pesan.length());				
				System.out.println("coin : "+coin);
				String message = new LineBot().genData(coin);
				if(message==null || message.equals(""))
					message = coin.toUpperCase()+" not found data";
				String replyToken = messageEvent.getReplyToken();
				balasChatDenganRandomJawaban(replyToken, message,coin);
			}else if(pesan.equals("atenpunk")){
				String jawaban = getRandomJawaban();
				String replyToken = messageEvent.getReplyToken();
				balasChatDenganRandomJawaban(replyToken, jawaban,null);
			}
		}catch (Exception e) {
			System.out.println("ERROR : "+e.getMessage());
		}

	}

	private String getRandomJawaban(){
		String jawaban = "";
		int random = new Random().nextInt();
		if(random%2==0){
			jawaban = "Hi";
		} else{
			jawaban = "Hello";
		}
		return jawaban;
	}

	private void balasChatDenganRandomJawaban(String replyToken, String jawaban, String coin){
		List<Message> messages = new ArrayList<Message>();
		TextMessage jawabanDalamBentukTextMessage = new TextMessage(jawaban);
		messages.add(jawabanDalamBentukTextMessage);
		if(coin!=null && !coin.trim().equals("")) {
			String fileName = System.getProperty("user.dir") + "/img" + File.separator + coin+".png";
			System.out.println(fileName);
			File file = new File(fileName);
			if(!file.exists()) {
				ImageMessage imageMessage = new ImageMessage(fileName, fileName);
				messages.add(imageMessage);
			}
		}
		try {
			lineMessagingClient
			.replyMessage(new ReplyMessage(replyToken, messages))
			.get();
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("Ada error chat");
		}
	}

}

