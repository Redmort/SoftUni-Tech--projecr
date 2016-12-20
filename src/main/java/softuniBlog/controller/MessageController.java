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
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.MessageBindingModel;
import softuniBlog.entity.Message;
import softuniBlog.entity.User;
import softuniBlog.repository.MessageRepository;
import softuniBlog.repository.UserRepository;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile/message/create/{id}")
    @PreAuthorize("isAuthenticated()")
    public String create (Model model, @PathVariable Integer id){
        if (!this.userRepository.exists(id)){
            return "redirect:/";
        }

        User user = this.userRepository.findOne(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User author = this.userRepository.findByEmail(principal.getUsername());

        if (user.getId() == author.getId()){
            return "redirect:/";
        }

        model.addAttribute("user", author);
        model.addAttribute("messageUser", user);
        model.addAttribute("view", "message/create");

        return "base-layout";
    }

    @PostMapping("/profile/message/create/{id}")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(@PathVariable Integer id, MessageBindingModel messageBindingModel){
        if (!this.userRepository.exists(id)){
            return "redirect:/";
        }

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User author = this.userRepository.findByEmail(principal.getUsername());

        User receive = this.userRepository.findOne(id);

        Date date = new Date();

        String status = "not read";

        Message messageEntity = new Message(
                messageBindingModel.getTitle(),
                messageBindingModel.getContent(),
                author,
                receive,
                date,
                status
        );

        this.messageRepository.saveAndFlush(messageEntity);

        return "redirect:/profile/" + receive.getId();
    }

    @GetMapping("/inbox/{id}")
    @PreAuthorize("isAuthenticated()")
    public String inbox(@PathVariable Integer id, Model model){
        if (!this.userRepository.exists(id)){
            return "redirect:/";
        }

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        User author = this.userRepository.findOne(id);

        if (!(user.getId().equals(author.getId()))) {

           return "redirect:/" ;
        }



        List<Message> messages = new ArrayList<>();
        for (Message message : author.getReceiveMessages()){
            messages.add(message);
        }

        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        model.addAttribute("user", user);
        model.addAttribute("author", author);
        model.addAttribute("messages", messages);
        model.addAttribute("view", "message/inbox");

        return "base-layout";
    }

    @GetMapping("/inbox/subject/{id}")
    @PreAuthorize("isAuthenticated()")
    public String inboxSubject(@PathVariable Integer id, Model model){
        if (!this.messageRepository.exists(id)){
            return "redirect:/";
        }

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        Message message = this.messageRepository.findOne(id);

        User author = message.getReceive();

        if (!(user.getId().equals(author.getId()))) {

            return "redirect:/" ;
        }

        String status = "read";

        message.setStatus(status);

        this.messageRepository.saveAndFlush(message);

        model.addAttribute("user", user);
        model.addAttribute("message", message);
        model.addAttribute("view", "message/subject");

        return "base-layout";
    }

    @GetMapping("/sent/{id}")
    @PreAuthorize("isAuthenticated()")
    public String sent(@PathVariable Integer id, Model model){
        if (!this.userRepository.exists(id)){
            return "redirect:/";
        }

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        User author = this.userRepository.findOne(id);


        if (!(user.getId().equals(author.getId()))) {

            return "redirect:/" ;
        }

        List<Message> messages = new ArrayList<>();
        for (Message message : user.getSendMessages()){
            messages.add(message);
        }

        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        model.addAttribute("user", user);
        model.addAttribute("author", author);
        model.addAttribute("messages", messages);
        model.addAttribute("view", "message/sent");

        return "base-layout";
    }

    @GetMapping("/sent/subject/{id}")
    @PreAuthorize("isAuthenticated()")
    public String sentSubject(@PathVariable Integer id, Model model){
        if (!this.messageRepository.exists(id)){
            return "redirect:/";
        }

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        Message message = this.messageRepository.findOne(id);

        User author = message.getReceive();

        User receive = message.getAuthor();

        if (!(user.getId().equals(receive.getId()))) {

            return "redirect:/" ;
        }

        model.addAttribute("receive", receive);
        model.addAttribute("user", user);
        model.addAttribute("message", message);
        model.addAttribute("view", "message/sentSubject");

        return "base-layout";
    }


}
