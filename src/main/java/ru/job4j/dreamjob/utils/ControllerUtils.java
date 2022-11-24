package ru.job4j.dreamjob.utils;

import ru.job4j.dreamjob.model.User;

import javax.servlet.http.HttpSession;

public class ControllerUtils {

    private static final String GUEST = "Гость";

    public static User checkUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setEmail(GUEST);
        }
        return user;
    }

}
