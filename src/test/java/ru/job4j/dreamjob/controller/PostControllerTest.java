package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.ui.Model;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.PostService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostControllerTest {

    private PostService postService;
    private CityService cityService;
    private Model model;
    private HttpSession session;
    private PostController postController;
    private List<City> cities;
    private List<Post> posts;
    private List<User> users;

    @BeforeEach
    private void initVars() {
        postService = mock(PostService.class);
        cityService = mock(CityService.class);
        model = mock(Model.class);
        session = mock(HttpSession.class);
        postController = new PostController(
                postService,
                cityService
        );

        cities = Arrays.asList(
                new City(1, "City1"),
                new City(2, "City2")
        );
        posts = Arrays.asList(
                new Post(1,
                        "New post",
                        "New description",
                        LocalDateTime.now(),
                        false,
                        cities.get(0)),
                new Post(2,
                        "New post2",
                        "New description2",
                        LocalDateTime.now(),
                        false,
                        cities.get(1))
        );
        users = Arrays.asList(
                new User(1, "email", "password"),
                new User(2, "email2", "password2")
        );

    }

    @Test
    void whenPosts() {
        User user = users.get(0);

        when(postService.findAll()).thenReturn(posts);
        when(session.getAttribute("user")).thenReturn(user);

        String page = postController.posts(model, session);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("posts", posts);
        assertThat(page).isEqualTo("posts");
    }

    @Test
    void whenAddPost() {
        User user = users.get(0);
        LocalDateTime create = LocalDateTime.now();
        Post post = new Post(0, "Заполните название", "Заполните описание", create, false, new City(0, "Выберите город"));

        when(session.getAttribute("user")).thenReturn(user);
        when(cityService.getAllCities()).thenReturn(cities);

        try (MockedStatic<LocalDateTime> localDateTime = mockStatic(LocalDateTime.class)) {
            localDateTime.when(LocalDateTime::now).thenReturn(create);

            String page = postController.addPost(model, session);

            verify(model).addAttribute("user", user);
            verify(model).addAttribute("post", post);
            verify(model).addAttribute("cities", cities);

            assertThat(page).isEqualTo("addPost");
        }
    }

    @Test
    void whenCreatePost() {
        Post postOut = posts.get(0);
        City city = new City(postOut.getCity().getId(), null);
        Post postIn = new Post(postOut.getId(),
                postOut.getName(),
                postOut.getDescription(),
                null,
                postOut.isVisible(),
                city);

        try (MockedStatic<LocalDateTime> localDateTime = mockStatic(LocalDateTime.class)) {
            localDateTime.when(LocalDateTime::now).thenReturn(postOut.getCreate());

            when(cityService.findById(postIn.getCity().getId())).thenReturn(cities.get(0));

            String page = postController.createPost(postIn);

            verify(postService).add(postOut);

            assertThat(page).isEqualTo("redirect:/posts");
        }
    }

    @Test
    void whenFormUpdatePost() {
        User user = users.get(0);
        Post post = posts.get(0);

        when(session.getAttribute("user")).thenReturn(user);
        when(postService.findById(post.getId())).thenReturn(post);
        when(cityService.getAllCities()).thenReturn(cities);

        String page = postController.formUpdatePost(model, session, post.getId());

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("post", post);
        verify(model).addAttribute("cities", cities);

        assertThat(page).isEqualTo("updatePost");
    }

    @Test
    void whenUpdatePost() {
        Post postOut = posts.get(0);
        City city = new City(postOut.getCity().getId(), null);
        Post postIn = new Post(postOut.getId(),
                postOut.getName(),
                postOut.getDescription(),
                postOut.getCreate(),
                postOut.isVisible(),
                city);

        when(cityService.findById(postIn.getCity().getId())).thenReturn(postOut.getCity());

        String page = postController.updatePost(postIn);

        verify(postService).update(postOut);

        assertThat(page).isEqualTo("redirect:/posts");
    }
}