package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.dreamjob.model.User;

import javax.servlet.http.HttpSession;

@ThreadSafe
@Controller
public class IndexControl {

    private static final String GUEST = "Гость";

    @GetMapping("/index")
    public String index(Model model, HttpSession session) {
        model.addAttribute("user", checkUser(session));
        return "index";
    }

    public static User checkUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setEmail(GUEST);
        }
        return user;
    }


}