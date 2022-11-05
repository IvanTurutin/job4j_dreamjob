package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Post;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PostStore {

    private static final PostStore INST = new PostStore();

    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final AtomicInteger id = new AtomicInteger();

    private PostStore() {
        posts.put(id.incrementAndGet(), new Post(id.get(), "Junior Java Job", "Description for Junior Java Job", LocalDateTime.now()));
        posts.put(id.incrementAndGet(), new Post(id.get(), "Middle Java Job", "Description for Middle Java Job", LocalDateTime.now()));
        posts.put(id.incrementAndGet(), new Post(id.get(), "Senior Java Job", "Description for Senior Java Job", LocalDateTime.now()));
    }

    public static PostStore instOf() {
        return INST;
    }

    public void add(Post post) {
        post.setId(id.incrementAndGet());
        posts.putIfAbsent(post.getId(), post);
    }

    public Post findById(int id) {
        return posts.get(id);
    }

    public void update(Post post) {
        posts.put(post.getId(), post);
    }

    public Collection<Post> findAll() {
        return posts.values();
    }
}