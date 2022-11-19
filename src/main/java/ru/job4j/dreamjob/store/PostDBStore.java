package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
@Repository
public class PostDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(PostDBStore.class.getName());

    private static final String TABLE_NAME_POSTS = "posts";
    private static final String TABLE_NAME_CITIES = "cities";
    private static final String TRUNCATE_TABLE = String.format("TRUNCATE TABLE %s RESTART IDENTITY", TABLE_NAME_POSTS);
    private static final String SELECT_STATEMENT = String.format(
            "SELECT p.id as post_id, "
            + "p.*, "
            + "c.name as city_name "
            + "FROM %s as p "
            + "JOIN %s as c "
            + "ON p.city_id = c.id ",
            TABLE_NAME_POSTS,
            TABLE_NAME_CITIES);
    private static final String FIND_ALL_STATEMENT = SELECT_STATEMENT + "ORDER BY post_id";
    private static final String FIND_BY_ID_STATEMENT = SELECT_STATEMENT + "WHERE p.id = ?";
    private static final String ADD_STATEMENT = String.format("INSERT INTO %s(name, description, date, visible, city_id) "
            + "VALUES (?, ?, ?, ?, ?)", TABLE_NAME_POSTS);
    private static final String UPDATE_STATEMENT = String.format(
            "UPDATE %s "
            + "SET name = ?, "
            + "description = ?, "
            + "date = ?, "
            + "visible = ?, "
            + "city_id = ? "
            + "WHERE id = ?", TABLE_NAME_POSTS);

    public PostDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Collection<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(FIND_ALL_STATEMENT)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(createPost(it));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
        return posts;
    }

    public Post add(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(ADD_STATEMENT,
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
             PreparedStatement ps =  cn.prepareStatement(UPDATE_STATEMENT)
        ) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(post.getCreate()));
            ps.setBoolean(4, post.isVisible());
            ps.setInt(5, post.getCity().getId());
            ps.setInt(6, post.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
    }

    public Post findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(FIND_BY_ID_STATEMENT)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return createPost(it);
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
        return null;
    }

    private Post createPost(ResultSet it) {
        try {
            return new Post(
                    it.getInt("id"),
                    it.getString("name"),
                    it.getString("description"),
                    it.getTimestamp("date").toLocalDateTime(),
                    it.getBoolean("visible"),
                    new City(it.getInt("city_id"), it.getString("city_name"))
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void truncateTable() {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(TRUNCATE_TABLE)
        ) {
            ps.execute();
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
    }
}
