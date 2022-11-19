package ru.job4j.dreamjob.store;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

public class PostDBStoreTest {

    private static PostDBStore store;

    @BeforeAll
    public static void initStore() {
        store = new PostDBStore(new Main().loadPool());
    }

    @AfterEach
    public void truncateTable() {
        store.truncateTable();
    }

    @Test
    public void whenCreatePost() {
        Post post = new Post(
                0,
                "Java Job",
                "Description for Job",
                LocalDateTime.now(),
                false,
                new City(1, "")
        );
        store.add(post);
        Post postInDb = store.findById(post.getId());
        assertThat(postInDb.getName()).isEqualTo(post.getName());
        assertThat(postInDb.getCity().getName()).isEqualTo("Москва");
    }

    @Test
    public void whenFindAllPosts() {
        Post post = new Post(
                0,
                "Java Job",
                "Description for Job",
                LocalDateTime.now(),
                false, new City(1, "")
        );
        Post post2 = new Post(
                0,
                "Java Job2",
                "Description for Job2",
                LocalDateTime.now(),
                false,
                new City(2, "")
        );
        store.add(post);
        store.add(post2);
        Collection<Post> postsInDb = store.findAll();
        assertThat(postsInDb).isNotEmpty().hasSize(2).contains(post, post2);
    }

    @Test
    public void whenUpdatePost() {
        Post post = new Post(
                0,
                "Java Job",
                "Description for Job",
                LocalDateTime.now(),
                false,
                new City(1, "")
        );
        store.add(post);
        String expectedName = "Java Job update";
        post.setName(expectedName);
        store.update(post);
        assertThat(store.findById(post.getId()).getName()).isEqualTo(expectedName);
    }
}