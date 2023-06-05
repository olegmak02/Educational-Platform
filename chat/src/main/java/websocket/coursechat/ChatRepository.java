package websocket.coursechat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Integer> {

    @Query(value = "SELECT * FROM message WHERE course_id = ?1 ORDER BY date", nativeQuery = true)
    List<ChatMessage> findAllByCourse(Integer courseId);
}
