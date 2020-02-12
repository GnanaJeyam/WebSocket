package com.jeyam.websocket;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@EnableWebSocket
@Configuration
/**
 * <p>
 * This class will enable the web socket
 * in default port.
 * Socket URL: ws://localhost:8080/socket
 * </p>
 * @author GnanaJeyam
 *
 */
public class WebSocketConfig implements WebSocketConfigurer{

	private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
	private static final List<WebSocketSession> sessionList = new CopyOnWriteArrayList<>();
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new WebSocketHandler(),"/socket").setAllowedOrigins("*");
	}

	/**
	 * 
	 * <p>
	 * This is the Handler class used to 
	 * handle the messages from the client
	 * Application.
	 * Once the message from client invoked
	 * handleTextMessage method will be called.
	 * </p>
	 *
	 */
	static class WebSocketHandler extends TextWebSocketHandler {

		@Override
		protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
			logger.info("Socket: Receiving message and sending to " + sessionList.size() + " clients");
			for (WebSocketSession webSocketSession : sessionList) {
				if (webSocketSession.isOpen()) {
					webSocketSession.sendMessage(new TextMessage("Hello Client" + message.getPayload()));
				} else {
					webSocketSession.close();
				}
			}
		}

		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			sessionList.add(session);
		}
	}
	
	/**
	 * 
	 * <p>
	 * Web dependency is presented in my classpath.
	 * To Disable the authentication for all endpoints,
	 * added this Configuration class.
	 * </p>
	 *
	 */
	@Configuration
	static class WebSecurityConfig extends WebSecurityConfigurerAdapter{
		@Override
	    protected void configure(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception{
	    	http.authorizeRequests()
			.antMatchers("/**").permitAll()
			.and().csrf().disable();
	    }
	}
	
}
