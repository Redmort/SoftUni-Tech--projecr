package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.CategoryBindingModel;
import softuniBlog.bindingModel.CommentBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Comment;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CommentRepository;
import softuniBlog.repository.UserRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Controller
public class CommentController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/article/{id}")
    @PreAuthorize("isAuthenticated()")
    public String create (CommentBindingModel commentBindingModel, @PathVariable Integer id){

        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        String likedUsers = userEntity.getId().toString();

        Article article = this.articleRepository.findOne(id);

        Date date = new Date();

        Integer commentsLike = 0;

        Comment commentEntity = new Comment(
                commentBindingModel.getContent(),
                userEntity,
                article,
                date,
                commentsLike,
                likedUsers
        );

        this.commentRepository.saveAndFlush(commentEntity);

        return "redirect:/article/" + article.getId() ;

    }

    @GetMapping("/article/comments/like/{id}")
    @PreAuthorize("isAuthenticated()")
    public String commentLike(@PathVariable Integer id){
        if (!this.commentRepository.exists(id)) {
            return "redirect:/";
        }

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userEntity = this.userRepository.findByEmail(user.getUsername());
        Comment comment = commentRepository.findOne(id);
        Integer articleId = comment.getArticle().getId();

        Set<String> likes = new HashSet<String>(Arrays.asList(comment.getLikedUsers().split(",")));

        if (userEntity.isLiked(likes)){
            return "redirect:/article/" + articleId;
        }

        String likedUsers = comment.getLikedUsers() + "," + userEntity.getId();

        Integer commentLike = comment.getCommentsLike() + 1;

        comment.setCommentsLike(commentLike);
        comment.setLikedUsers(likedUsers);

        this.commentRepository.saveAndFlush(comment);


        return "redirect:/article/" + articleId;
    }
}
