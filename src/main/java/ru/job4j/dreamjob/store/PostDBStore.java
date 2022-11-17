package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
@Repository
public class PostDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(PostDBStore.class.getName());

    public PostDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "SELECT p.id as post_id, "
                             + "p.name as post_name, "
                             + "p.description as post_description, "
                             + "p.date as post_date, "
                             + "p.visible as post_visible, "
                             + "p.city_id as post_city_id, "
                             + "c.name as city_name "
                             + "FROM posts as p "
                             + "JOIN cities as c "
                             + "ON p.city_id = c.id "
                             + "ORDER BY post_id"
             )
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(
                            new Post(
                                    it.getInt("post_id"),
                                    it.getString("post_name"),
                                    it.getString("post_description"),
                                    it.getTimestamp("post_date").toLocalDateTime(),
                                    it.getBoolean("post_visible"),
                                    new City(it.getInt("post_city_id"), it.getString("city_name"))
                            )
                    );
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
        return posts;
    }

    public Post add(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "INSERT INTO posts(name, description, date, visible, city_id) "
                             + "VALUES (?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(post.getCreate()));
            ps.setBoolean(4, post.isVisible());
            ps.setInt(5, post.getCity().getId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
        return post;
    }

    public void update(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "UPDATE posts "
                             + "SET name = ?, "
                             + "description = ?, "
                             + "date = ?, "
                             + "visible = ?, "
                             + "city_id = ? "
                             + "WHERE id = ?")
        ) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(post.getCreate()));
            ps.setBoolean(4, post.isVisible());
            ps.setInt(5, post.getCity().getId());
            ps.setInt(6, post.getId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
    }

    public Post findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "SELECT p.id as post_id, "
                             + "p.name as post_name, "
                             + "p.description as post_description, "
                             + "p.date as post_date, "
                             + "p.visible as post_visible, "
                             + "p.city_id as post_city_id, "
                             + "c.name as city_name "
                             + "FROM posts as p "
                             + "JOIN cities as c "
                             + "ON p.city_id = c.id "
                             + "WHERE p.id = ?"
             )
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Post(
                            it.getInt("post_id"),
                            it.getString("post_name"),
                            it.getString("post_description"),
                            it.getTimestamp("post_date").toLocalDateTime(),
                            it.getBoolean("post_visible"),
                            new City(it.getInt("post_city_id"), it.getString("city_name"))
                    );
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
        return null;
    }
}
