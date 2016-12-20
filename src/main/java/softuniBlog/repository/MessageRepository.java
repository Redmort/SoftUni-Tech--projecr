package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuniBlog.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
