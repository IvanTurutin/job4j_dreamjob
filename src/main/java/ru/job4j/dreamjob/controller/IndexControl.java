package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.utils.ControllerUtils;

import javax.servlet.http.HttpSession;

@ThreadSafe
@Controller
public class IndexControl {


    @GetMapping("/index")
    public String index(Model model, HttpSession session) {
        model.addAttribute("user", ControllerUtils.checkUser(session));
        return "index";
    }




}