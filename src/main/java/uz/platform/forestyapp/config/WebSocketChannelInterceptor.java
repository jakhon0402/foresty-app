package uz.platform.forestyapp.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import uz.platform.forestyapp.security.JwtProvider;
import uz.platform.forestyapp.service.AuthService;

import java.security.Principal;

@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthService authService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String token = accessor.getFirstNativeHeader("Authorization");

            if(token!=null && token.startsWith("Bearer")) {
                token = token.substring(7);
                boolean validateToken = jwtProvider.validateAccessToken(token);
                if (validateToken) {
                    String username = jwtProvider.getUsernameFromAccessToken(token);

                    UserDetails userDetails = authService.loadUserByUsername(username);

                    Principal usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    accessor.setLeaveMutable(false);
                    accessor.setUser(usernamePasswordAuthenticationToken);

                }
                else {
                    throw new IllegalArgumentException("Ruxsat yo'q");
                }
            }
            else {
                throw new IllegalArgumentException("Ruxsat yo'q");
            }
        }
        return message;
    }
}
