package softuniBlog.controller;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import softuniBlog.bindingModel.UserBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.RoleRepository;
import softuniBlog.repository.UserRepository;

import javax.imageio.IIOException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("view", "user/register");

        return "base-layout";
    }

    @PostMapping("/register")
    public String registerProcess(UserBindingModel userBindingModel){

        if(!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())){
            return "redirect:/register";
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String picture = "/images/securelife-user.jpg";

        String description = null;

        User user = new User(
                userBindingModel.getEmail(),
                userBindingModel.getFullName(),
                bCryptPasswordEncoder.encode(userBindingModel.getPassword()),
                picture
        );

        File file = new File("C:\\Users\\Plamen Admin\\Desktop\\java\\blog\\src\\main\\resources\\static\\images\\securelife-user.jpg");

        //user.setPicture("/images/securelife-user.jpg");

        Role userRole = this.roleRepository.findByName("ROLE_USER");

        user.addRole(userRole);

        this.userRepository.saveAndFlush(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("view", "user/login");

        return "base-layout";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login?logout";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());
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

        boolean isThisUser = true;
        boolean isProfileUser = false;

        model.addAttribute("isProfileUser", isProfileUser);
        model.addAttribute("profileUser", user);
        model.addAttribute("isThisUser", isThisUser);
        model.addAttribute("articles", articles);
        model.addAttribute("user", user);
        model.addAttribute("view", "user/profile");

        return "base-layout";
    }

    @GetMapping("/profile/editPicture")
    @PreAuthorize("isAuthenticated()")
    public String editPicture(Model model){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("view", "user/editPicture");

        return "base-layout";
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String UploadFile(UserBindingModel userBindingModel){

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        MultipartFile file = userBindingModel.getPicture();
        if (file != null){
            String originalFileName = user.getId() + file.getOriginalFilename();
             File imageFile=new File("C:\\Users\\Plamen Admin\\Desktop\\java\\blog\\src\\main\\resources\\static\\images\\", originalFileName);

            try {
                file.transferTo(imageFile);
                user.setPicture("/images/" + originalFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.userRepository.saveAndFlush(user);

        return "redirect:/profile";
    }

    @GetMapping("/profile/editDescription")
    @PreAuthorize("isAuthenticated()")
    public String editDescription(Model model){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        String description = user.getDescription();

        model.addAttribute("description", description);
        model.addAttribute("user", user);
        model.addAttribute("view", "user/editDescription");

        return "base-layout";
    }

    @PostMapping("/profile/editDescription")
    @PreAuthorize("isAuthenticated()")
    public String editDescriptionProcess(UserBindingModel userBindingModel){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        String description = userBindingModel.getDescription();

        user.setDescription(description);

        this.userRepository.saveAndFlush(user);

        return "redirect:/profile";
    }

}

