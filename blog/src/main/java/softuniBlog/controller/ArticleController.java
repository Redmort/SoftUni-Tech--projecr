package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.ArticleBindingModel;
import softuniBlog.bindingModel.UserBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Category;
import softuniBlog.entity.Comment;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.CommentRepository;
import softuniBlog.repository.UserRepository;

import javax.jws.soap.SOAPBinding;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String create (Model model){

        List<Category> categories = this.categoryRepository.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("view", "article/create");

        return "base-layout";
    }

    @PostMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(ArticleBindingModel articleBindingModel) {

        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                                                                   .getAuthentication()
                                                                   .getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        String likedUsers = userEntity.getId().toString();

        Category category = this.categoryRepository.findOne(articleBindingModel.getCategoryId());

        Date date = new Date();

        String status = "Created:";

        Integer articleLike = 0;


        Article articleEntity = new Article(
                articleBindingModel.getTitle(),
                articleBindingModel.getContent(),
                userEntity,
                category,
                date,
                status,
                articleLike,
                likedUsers
        );

        this.articleRepository.saveAndFlush(articleEntity);

        return "redirect:/";

    }

    @GetMapping("/article/{id}")
    public String details(Model model, @PathVariable Integer id) {

        if (!this.articleRepository.exists(id)){

            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);
        //Set<Comment> comments = article.getComments();
        List<Comment> comments = new ArrayList<>();

        for (Comment comment : article.getComments()){
            comments.add(comment);
        }

        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        Set<String> likes = new HashSet<String>(Arrays.asList(article.getLikedUsers().split(",")));

        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            User entityUser = this.userRepository.findByEmail(principal.getUsername());

            String text ="Be the first who like it!";
            Boolean isNotEmpty = false;

            if (entityUser.isAuthor(article)&& article.likeCount(likes)>1){
                text = "Likes List";
                isNotEmpty = true;
            }
            else if (entityUser.isAuthor(article)&& article.likeCount(likes) == 1){
                text = "No likes!";
                isNotEmpty = false;
            }
            else if  (!entityUser.isAuthor(article)&& article.likeCount(likes)>1 && entityUser.isLiked(likes)){
                text = "You and ... liked this";
                isNotEmpty = true;
            }
            else if (!entityUser.isAuthor(article)&& article.likeCount(likes)>1 && !entityUser.isLiked(likes)){
                text = "Likes List";
                isNotEmpty = true;
            }


            model.addAttribute("isNotEmpty", isNotEmpty);
            model.addAttribute("text", text);
            model.addAttribute("user", entityUser );
        }

        model.addAttribute("likes", likes);
        model.addAttribute("comments", comments);
        model.addAttribute("article", article);
        model.addAttribute("view", "article/details");

        return "base-layout";
    }

    @GetMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit (@PathVariable Integer id, Model model){

        if (!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if (!isUserOrAdmin(article)){
            return "redirect:/article/" + id;
        }
        List<Category> categories = this.categoryRepository.findAll();

        model.addAttribute("article", article);
        model.addAttribute("view", "article/edit");
        model.addAttribute("categories", categories);

        return "base-layout";
    }

    @PostMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id, ArticleBindingModel articleBindingModel){

        if (!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        Article article = articleRepository.findOne(id);

        if (!isUserOrAdmin(article)){
            return "redirect:/article/" + id;
        }

        Category category = this.categoryRepository.findOne(articleBindingModel.getCategoryId());
        Date date = new Date();
        String status = "Edited:";

        article.setCategory(category);
        article.setContent(articleBindingModel.getContent());
        article.setTitle(articleBindingModel.getTitle());
        article.setDate(date);
        article.setStatus(status);

        this.articleRepository.saveAndFlush(article);

        return "redirect:/article/" + article.getId();
    }

    @GetMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete (Model model,@PathVariable Integer id){

        if (!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        Article article = articleRepository.findOne(id);

        if (!isUserOrAdmin(article)){
            return "redirect:/article/" + id;
        }

        model.addAttribute("article", article);
        model.addAttribute("view", "article/delete");

        return "base-layout";
    }

    @PostMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id) {

        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = articleRepository.findOne(id);

        if (!isUserOrAdmin(article)){
            return "redirect:/article/" + id;
        }

        for (Comment comment : article.getComments()) {
            this.commentRepository.delete(comment);
        }

        this.articleRepository.delete(article);

        return "redirect:/";
    }

    @GetMapping("/article/like/{id}")
    @PreAuthorize("isAuthenticated()")
    public String articleLike(@PathVariable Integer id){
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userEntity = this.userRepository.findByEmail(user.getUsername());
        Article article = articleRepository.findOne(id);

        Set<String> likes = new HashSet<String>(Arrays.asList(article.getLikedUsers().split(",")));

        if (userEntity.isLiked(likes)){
            return "redirect:/article/" + article.getId();
        }

        String likedUsers = article.getLikedUsers() + "," + userEntity.getId();

        Integer articleLike = article.getArticleLike() + 1;

        article.setArticleLike(articleLike);
        article.setLikedUsers(likedUsers);

        this.articleRepository.saveAndFlush(article);


        return "redirect:/article/" + article.getId();


    }

    @GetMapping("/article/likeList/{id}")
    @PreAuthorize("isAuthenticated()")
    public String likeList(Model model, @PathVariable Integer id){
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);
        String[] likes = article.getLikedUsers().split(",");

        List<User> names = new ArrayList<>();
        for (int i = 0; i <likes.length ; i++) {
            Integer num = Integer.valueOf(likes[i]);
            User user =this.userRepository.findOne(num);
            names.add(user);
        }

        model.addAttribute("article", article);
        model.addAttribute("names", names);
        model.addAttribute("view", "/article/likeList");
        return "base-layout";
    }

    private boolean isUserOrAdmin(Article article) {

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        return userEntity.isAdmin() || userEntity.isAuthor(article);
    }

}
