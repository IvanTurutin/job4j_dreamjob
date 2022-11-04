package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.PostStore;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
public class PostController {

    private final PostStore postStore = PostStore.instOf();

    @GetMapping("/posts")
    public String posts(Model model) {
        model.addAttribute("posts", postStore.findAll());
        return "posts";
    }

    @GetMapping("/formAddPost")
    public String addPost(Model model) {
        model.addAttribute("post", new Post(0, "Заполните название", "Заполните описание", LocalDateTime.now()));
        return "addPost";
    }

    @PostMapping("/createPost")
    public String createPost(@ModelAttribute Post post) {
/*
        int id = Integer.parseInt(req.getParameter("id"));
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        System.out.println("id = " + id + ", " + "name = " + name + ", " + "description = " + description);
        postStore.add(new Post(id, name, description, LocalDateTime.now()));
*/
        System.out.println(post);
        postStore.add(post);
        return "redirect:/posts";
    }
}