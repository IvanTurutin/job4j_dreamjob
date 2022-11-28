package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;
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

    @Test
    void whenPosts() {
        PostService postService = mock(PostService.class);
        CityService cityService = mock(CityService.class);
        PostController postController = new PostController(postService, cityService);
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        List<Post> posts = Arrays.asList(
                new Post(1,
                        "New post",
                        "New description",
                        LocalDateTime.now(),
                        false,
                        new City(1, "City1")),
                new Post(2,
                        "New post2",
                        "New description2",
                        LocalDateTime.now(),
                        false,
                        new City(2, "City2"))
        );
        User user = new User(1, "email", "password");

        when(postService.findAll()).thenReturn(posts);
        when(session.getAttribute("user")).thenReturn(user);

        String page = postController.posts(model, session);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("posts", posts);
        assertThat(page).isEqualTo("posts");
    }

    @Test
    void whenAddPost() {
        PostService postService = mock(PostService.class);
        CityService cityService = mock(CityService.class);
        PostController postController = new PostController(postService, cityService);
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        List<City> cities = Arrays.asList(
                new City(1, "City1"),
                new City(2, "City2")
        );
        User user = new User(1, "email", "password");
        Post post = new Post(0,
                "Заполните название",
                "Заполните описание",

                LocalDateTime.now(),
                false,
                new City(0, "Выберите город"));

        when(session.getAttribute("user")).thenReturn(user);
        when(cityService.getAllCities()).thenReturn(cities);

        String page = postController.addPost(model, session);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("post", post);
        verify(model).addAttribute("cities", cities);

        assertThat(page).isEqualTo("addPost");
    }

    @Test
    void whenCreatePost() {
        PostService postService = mock(PostService.class);
        CityService cityService = mock(CityService.class);
        PostController postController = new PostController(postService, cityService);
        City cityOut = new City(1, "City1");
        Post postOut = new Post(1,
                "New post",
                "New description",
                LocalDateTime.now(),
                false,
                cityOut);
        City cityIn = new City(postOut.getCity().getId(), null);
        Post postIn = new Post(postOut.getId(),
                postOut.getName(),
                postOut.getDescription(),
                null,
                postOut.isVisible(),
                cityIn);

        when(cityService.findById(postIn.getCity().getId())).thenReturn(cityOut);

        String page = postController.createPost(postIn);

        verify(postService).add(postOut);

        assertThat(page).isEqualTo("redirect:/posts");
    }

    @Test
    void whenFormUpdatePost() {
        PostService postService = mock(PostService.class);
        CityService cityService = mock(CityService.class);
        PostController postController = new PostController(postService, cityService);
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        List<City> cities = Arrays.asList(
                new City(1, "City1"),
                new City(2, "City2")
        );
        User user = new User(1, "email", "password");
        Post post = new Post(1,
                "New post",
                "New description",
                LocalDateTime.now(),
                false,
                new City(1, "City1"));

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
        PostService postService = mock(PostService.class);
        CityService cityService = mock(CityService.class);
        PostController postController = new PostController(postService, cityService);
        City cityOut = new City(1, "City1");
        Post postOut = new Post(1,
                "New post",
                "New description",
                LocalDateTime.now(),
                false,
                cityOut);

        City cityIn = new City(postOut.getCity().getId(), null);
        Post postIn = new Post(postOut.getId(),
                postOut.getName(),
                postOut.getDescription(),
                postOut.getCreate(),
                postOut.isVisible(),
                cityIn);

        when(cityService.findById(postIn.getCity().getId())).thenReturn(postOut.getCity());

        String page = postController.updatePost(postIn);

        verify(postService).update(postOut);

        assertThat(page).isEqualTo("redirect:/posts");
    }
}