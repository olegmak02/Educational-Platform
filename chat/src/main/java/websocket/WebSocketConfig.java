package websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JWTChecker jwtChecker;
    private final UserHandshakeHandler userHandshakeHandler;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/websocket")
            .setHandshakeHandler(userHandshakeHandler)
            .addInterceptors(new HandshakeInterceptor() {
              @Override
              public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                  if (request.getHeaders().get("cookie") == null) {
                      return false;
                  }

                  String token = request.getHeaders().get("cookie").get(0).split("token=")[1].split(";")[0];
                  String username = jwtChecker.checkToken(token);
                  return username != null;
              }

              @Override
              public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

              }
            })
            .withSockJS()
            .setClientLibraryUrl("//cdn.jsdelivr.net/sockjs/1.0.0/sockjs.min.js");
      }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic");
    registry.setApplicationDestinationPrefixes("/ws");
  }
}
