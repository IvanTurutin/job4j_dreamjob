package ru.job4j.dreamjob.store;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

class CandidateDBStoreTest {

    private static CandidateDBStore store;

    @BeforeAll
    public static void initStore() {
        store = new CandidateDBStore(new Main().loadPool());
    }

    @AfterEach
    public void truncateTable() {
        store.truncateTable();
    }

    @Test
    public void whenCreateCandidate() {
        Candidate candidate = new Candidate(
                0,
                "Candidate 1",
                "Description for Candidate 1",
                LocalDateTime.now(),
                false,
                new City(1, "")
        );
        System.out.println(candidate);
        store.add(candidate);
        System.out.println(candidate);
        Candidate candidateInDb = store.findById(candidate.getId());
        assertThat(candidateInDb.getName()).isEqualTo(candidate.getName());
        assertThat(candidateInDb.getCity().getName()).isEqualTo("Москва");
    }

    @Test
    public void whenFindAllCandidates() {
        Candidate candidate = new Candidate(
                0,
                "Candidate 1",
                "Description for Candidate 1",
                LocalDateTime.now(),
                false,
                new City(1, "")
        );
        Candidate candidate2 = new Candidate(
                0,
                "Candidate 2",
                "Description for Candidate 2",
                LocalDateTime.now(),
                false,
                new City(1, "")
        );
        store.add(candidate);
        store.add(candidate2);
        Collection<Candidate> candidatesInDb = store.findAll();
        assertThat(candidatesInDb).isNotEmpty().hasSize(2).contains(candidate, candidate2);
        store.truncateTable();
    }

    @Test
    public void whenUpdateCandidate() {
        Candidate candidate = new Candidate(
                0,
                "Candidate 1",
                "Description for Candidate 1",
                LocalDateTime.now(),
                false,
                new City(1, "")
        );
        store.add(candidate);
        String expectedName = "Candidate 1 update";
        candidate.setName(expectedName);
        store.update(candidate);
        assertThat(store.findById(candidate.getId()).getName()).isEqualTo(expectedName);
        store.truncateTable();
    }
}