package websocket.coursechat;

import static java.lang.String.format;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
@Controller
public class ChatController {

  private final ChatRepository chatRepository;
  @Autowired
  private SimpMessageSendingOperations messagingTemplate;

  @MessageMapping("/chat/{roomId}/sendMessage")
  public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage, Principal principal) {
    if (principal == null) {
      return;
    }

    chatMessage.setDate(new Date());
    chatMessage.setCourseId(Integer.parseInt(roomId));
    chatMessage.setSender(principal.getName());
    chatRepository.save(chatMessage);
    messagingTemplate.convertAndSend(format("/topic/%s", roomId), chatMessage);
  }

  @MessageMapping("/private")
  @SendToUser("/topic/private")
  public List<ChatMessage> getMessage(@Payload ChatMessage chatMessage,
                                      Principal principal) {
    return chatRepository.findAllByCourse(chatMessage.getCourseId());
  }

  @CrossOrigin
  @GetMapping("/chat")
  public String redirectToExternalUrl() {
    return "chat/main.html";
  }
}
