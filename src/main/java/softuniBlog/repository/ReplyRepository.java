package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuniBlog.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
}
