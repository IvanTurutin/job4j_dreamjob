package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@ThreadSafe
@Repository
public class UserDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(UserDBStore.class.getName());

    private static final String TABLE_NAME = "users";
    private static final String ADD_STATEMENT = String.format(
            "INSERT INTO %s(email, password) VALUES (?, ?)",
            TABLE_NAME);
    private static final String FIND_BY_EMAIL_STATEMENT = String.format("SELECT * FROM %s WHERE email = ?", TABLE_NAME);
    private static final String TRUNCATE_TABLE = String.format("TRUNCATE TABLE %s RESTART IDENTITY", TABLE_NAME);
    private static final String FIND_BY_EMAIL_AND_PASSWORD_STATEMENT = String.format(
            "SELECT * FROM %s WHERE email = ? AND password = ?",
            TABLE_NAME);


    public UserDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<User> add(User user) {
        Optional<User> optionalUser = Optional.empty();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(ADD_STATEMENT,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                    optionalUser = Optional.of(user);
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in UserDBStore", e);
        }
        return optionalUser;
    }

    public Optional<User> findByEmail(String email) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(FIND_BY_EMAIL_STATEMENT)) {
            ps.setString(1, email);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return Optional.of(createUser(it));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in UserDBStore", e);
        }
        return Optional.empty();
    }

    private User createUser(ResultSet it) throws SQLException {
        return new User(
                it.getInt("id"),
                it.getString("email"),
                it.getString("password"));
    }

    public Optional<User> findUserByEmailAndPassword(String email, String password) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(FIND_BY_EMAIL_AND_PASSWORD_STATEMENT)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return Optional.of(createUser(it));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in UserDBStore", e);
        }
        return Optional.empty();
    }

    public void truncateTable() {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(TRUNCATE_TABLE)
        ) {
            ps.execute();
        } catch (Exception e) {
            LOG.error("Exception in UserDBStore", e);
        }
    }
}
