package softuniBlog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import softuniBlog.bindingModel.CategoryBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Category;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String list(Model model){

        List<Category> categories = this.categoryRepository.findAll();

        categories = categories.stream()
                               .sorted(Comparator.comparingInt(Category::getId))
                               .collect(Collectors.toList());
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User entityUser = this.userRepository.findByEmail(principal.getUsername());

        model.addAttribute("user", entityUser );

        model.addAttribute("categories", categories);
        model.addAttribute("view", "admin/category/list");

        return "base-layout";
    }

    @GetMapping("/create")
    public String create(Model model){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User entityUser = this.userRepository.findByEmail(principal.getUsername());

        model.addAttribute("user", entityUser );
        model.addAttribute("view", "admin/category/create");

        return "base-layout";

    }

    @PostMapping("/create")
    public String createProcess(CategoryBindingModel categoryBindingModel){
        if (StringUtils.isEmpty(categoryBindingModel.getName())){
            return "redirect:/admin/categories/create";
        }

        Category category = new Category(categoryBindingModel.getName());

        this.categoryRepository.saveAndFlush(category);

        return "redirect:/admin/categories/";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable Integer id){
        if (!this.categoryRepository.exists(id)){
            return "redirect:/admin/categories/";
        }

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User entityUser = this.userRepository.findByEmail(principal.getUsername());

        model.addAttribute("user", entityUser );
        Category category= this.categoryRepository.findOne(id);

        model.addAttribute("category", category);
        model.addAttribute("view", "admin/category/edit");

        return "base-layout";
    }

    @PostMapping("/edit/{id}")
    public String editProcess(@PathVariable Integer id, CategoryBindingModel categoryBindingModel){
        if (!this.categoryRepository.exists(id)){
            return "redirect:/admin/categories/";
        }

        Category category = this.categoryRepository.findOne(id);

        category.setName(categoryBindingModel.getName());

        this.categoryRepository.saveAndFlush(category);

        return "redirect:/admin/categories/";
    }

    @GetMapping("/delete/{id}")
    public String delete (@PathVariable Integer id, Model model){
        if(!this.categoryRepository.exists(id)){
            return "redirect:/admin/categories/";
        }

        Category category = this.categoryRepository.findOne(id);
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User entityUser = this.userRepository.findByEmail(principal.getUsername());

        model.addAttribute("user", entityUser );

        model.addAttribute("category", category);
        model.addAttribute("view", "admin/category/delete");

        return "base-layout";
    }

    @PostMapping("/delete/{id}")
    public String deleteProcess(@PathVariable Integer id, CategoryBindingModel categoryBindingModel){
        if (!this.categoryRepository.exists(id)){
            return "redirect:/admin/categories/";
        }

        Category category = this.categoryRepository.findOne(id);

        for (Article article : category.getArticles()) {
            this.articleRepository.delete(article);
        }

        this.categoryRepository.delete(category);

        return "redirect:/admin/categories/";
    }
}
