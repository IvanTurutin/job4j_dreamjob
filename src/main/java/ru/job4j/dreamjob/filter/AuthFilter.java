package ru.job4j.dreamjob.filter;

import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Component
public class AuthFilter implements Filter {

    private final static Set<String> ALLOWABLE_URI = Set.of(
            "loginPage",
            "login",
            "formAddUser",
            "registration",
            "fail",
            "success"
    );

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        if (checkAllowableUri(uri)) {
            chain.doFilter(req, res);
            return;
        }
        if (req.getSession().getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/loginPage");
            return;
        }
        chain.doFilter(req, res);
    }

    private boolean checkAllowableUri(String uri) {
        for (String end : ALLOWABLE_URI) {
            if (uri.endsWith(end)) {
                return true;
            }
        }
        return false;
    }
}