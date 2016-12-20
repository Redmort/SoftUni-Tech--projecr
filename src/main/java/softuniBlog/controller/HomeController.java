package softuniBlog.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.expression.Lists;
import softuniBlog.entity.Article;
import softuniBlog.entity.Category;
import softuniBlog.entity.Message;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.UserRepository;

import javax.jws.soap.SOAPBinding;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model) {

        List<Article> articles = this.articleRepository.findAll();
        List<Category> categories = this.categoryRepository.findAll();

        Collections.sort(articles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            User entityUser = this.userRepository.findByEmail(principal.getUsername());

            model.addAttribute("user", entityUser );
        }

        model.addAttribute("view", "home/index");
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categories);
        return "base-layout";
    }

    @RequestMapping("/error/403")
    public String accessDenied(Model model){
        model.addAttribute("view", "error/403");

        return "base-layout";
    }

    @GetMapping("/category/{id}")
    public String listArticles(Model model, @PathVariable Integer id){
        if (!this.categoryRepository.exists(id)){
            return "redirect:/";
        }
        Category category = this.categoryRepository.findOne(id);
        //Set<Article> articles = category.getArticles();
        List<Article> articles = new ArrayList<>();

        for (Article article : category.getArticles()){
            articles.add(article);
        }

        Collections.sort(articles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            User entityUser = this.userRepository.findByEmail(principal.getUsername());

            model.addAttribute("user", entityUser );
        }

        model.addAttribute("articles", articles);
        model.addAttribute("category", category);
        model.addAttribute("view", "home/list-articles");

        return "base-layout";
    }

    @GetMapping("/profile/{id}")
    @PreAuthorize("isAuthenticated()")
    public String pofileId (Model model, @PathVariable Integer id){
        if (!this.userRepository.exists(id)){
            return "redirect:/";
        }

        User user = this.userRepository.findOne(id);

        List<Article> articles = new ArrayList<>();

        for (Article article: user.getArticles()) {

            articles.add(article);
        }

        Collections.sort(articles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User thisUser = this.userRepository.findByEmail(principal.getUsername());

        boolean isThisUser = user.getId()== thisUser.getId();
        boolean isProfileUser = user.getId()!= thisUser.getId();


        model.addAttribute("isProfileUser", isProfileUser);
        model.addAttribute("user", thisUser);
        model.addAttribute("isThisUser", isThisUser);
        model.addAttribute("articles", articles);
        model.addAttribute("profileUser", user);
        model.addAttribute("view", "user/profile");

        return "base-layout";

    }
}
