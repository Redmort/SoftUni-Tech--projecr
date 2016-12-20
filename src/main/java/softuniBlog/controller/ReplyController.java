package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.ReplyBindengModel;
import softuniBlog.entity.Comment;
import softuniBlog.entity.Reply;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CommentRepository;
import softuniBlog.repository.ReplyRepository;
import softuniBlog.repository.UserRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ReplyController {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/article/reply/{id}")
    @PreAuthorize("isAuthenticated()")
    public String create(ReplyBindengModel replyBindengModel, @PathVariable Integer id){

        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        Comment comment = this.commentRepository.findOne(id);

        Date date = new Date();

        String status = "public";

        Reply replyEntity = new Reply(
                replyBindengModel.getContent(),
                userEntity,
                comment,
                date,
                status
        );

        Integer articleId = comment.getArticle().getId();

        this.replyRepository.saveAndFlush(replyEntity);

        return "redirect:/article/" + articleId;
    }

    @PostMapping("/admin/censora/reply/{id}")
    @PreAuthorize("isAuthenticated()")
    public String censored (ReplyBindengModel replyBindengModel, @PathVariable Integer id){
        if (!this.replyRepository.exists(id)){
            return "redirect:/";
        }

        Reply reply = this.replyRepository.findOne(id);

        String statusId = "0";
        statusId = replyBindengModel.getStatusId();
        String status = " ";

        if (statusId.equals("1")){

            status = "public";
        }
        else {
            status = "censored";
        }

        reply.setStatus(status);
        this.replyRepository.saveAndFlush(reply);

        return "redirect:/article/" + reply.getComment().getArticle().getId();
    }



}
