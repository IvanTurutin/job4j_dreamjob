package ru.job4j.dreamjob.store;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class UserDBStoreTest {

    private static UserDBStore store;

    @BeforeAll
    public static void initStore() {
        store = new UserDBStore(new Main().loadPool());
    }

    @AfterEach
    public void truncateTable() {
        store.truncateTable();
    }

    @Test
    public void whenAddUser() {
        User user = new User(
                0,
                "ivan@email.com",
                "123456789"
        );
        store.add(user);

        Optional<User> userInDb = store.findByEmail(user.getEmail());
        assertThat(userInDb.isPresent()).isTrue();
        assertThat(userInDb.get().getEmail()).isEqualTo(user.getEmail());
    }
}