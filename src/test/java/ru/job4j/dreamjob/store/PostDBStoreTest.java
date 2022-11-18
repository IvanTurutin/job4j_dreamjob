package ru.job4j.dreamjob.store;

import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

public class PostDBStoreTest {
    @Test
    public void whenCreatePost() {
        PostDBStore store = new PostDBStore(new Main().loadPool());
        Post post = new Post(
                0,
                "Java Job",
                "Description for Job",
                LocalDateTime.now(),
                false,
                new City(1, "")
        );
        System.out.println(post);
        store.add(post);
        System.out.println(post);
        Post postInDb = store.findById(post.getId());
        assertThat(postInDb.getName()).isEqualTo(post.getName());
        store.truncateTable();
    }

    @Test
    public void whenFindAllPosts() {
        PostDBStore store = new PostDBStore(new Main().loadPool());
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
        store.truncateTable();
    }

    @Test
    public void whenUpdatePost() {
        PostDBStore store = new PostDBStore(new Main().loadPool());
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
        store.truncateTable();
    }
}