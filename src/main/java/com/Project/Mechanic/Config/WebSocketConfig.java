package com.Project.Mechanic.Config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.Project.Mechanic.Service.CustomUserDetailsService;
import com.Project.Mechanic.Service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;
	
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
	}
	
	@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
        		.setAllowedOriginPatterns("*");
    }
	
	
	@Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
        	
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // Ee interceptor prathi STOMP CONNECT frame ki trigger avuthundi
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    
                    // Frontend nunchi vachina 'Authorization' header ni extract chesthunnam
                    List<String> authHeaders = accessor.getNativeHeader("Authorization");
                    log.info("Token from the Frontend: {}", authHeaders);

                    if (authHeaders != null && !authHeaders.isEmpty()) {
                        String bearerToken = authHeaders.get(0);
                        
                        if (bearerToken.startsWith("Bearer ")) {
                            String token = bearerToken.substring(7);
                            
                            try {
                                String username = jwtService.extractUsername(token);
                                
                                if (username != null) {
                                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                    
                                    if (jwtService.isTokenValid(token, userDetails)) {
                                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                                userDetails, 
                                                null, 
                                                userDetails.getAuthorities()
                                        );
                                        
                                        // Token nunchi userId extract chesi session auth ki set chesthunnam
                                        Long userId = jwtService.extractUserId(token);
                                        auth.setDetails(userId);
                                        
                                        // WebSocket session ki user ni bind chesthunnam
                                        accessor.setUser(auth);
                                        log.info("WebSocket connected for User ID: {}", userId);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("WebSocket Authentication failed: {}", e.getMessage());
                            }
                        }
                    }
                }
                return message;
            }
        });
    }

}
