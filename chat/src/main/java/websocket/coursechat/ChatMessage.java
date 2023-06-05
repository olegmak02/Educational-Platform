package websocket.coursechat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "message")
@Data
@RequiredArgsConstructor
public class ChatMessage {

  @Id
  @GeneratedValue
  private Integer id;

  private String content;
  private String sender;
  private Integer courseId;
  private Date date;
}
