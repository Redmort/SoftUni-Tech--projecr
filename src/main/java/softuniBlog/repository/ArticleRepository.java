package softuniBlog.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import softuniBlog.entity.Article;

import java.time.LocalDate;

public interface ArticleRepository extends JpaRepository <Article, Integer>{
}
