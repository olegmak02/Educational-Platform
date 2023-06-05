package websocket;

import com.sun.security.auth.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Service
@Configurable
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Autowired
    private JWTChecker jwtChecker;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request.getHeaders().get("cookie") == null) {
            throw new RuntimeException("Cannot recognize user");
        }

        String token = request.getHeaders().get("cookie").get(0).split("token=")[1].split(";")[0];

        String username = jwtChecker.checkToken(token);

        if (username == null) {
            throw new RuntimeException("User not found");
        }

        return new UserPrincipal(username);
    }
}
