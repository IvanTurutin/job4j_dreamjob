package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class CandidateStore {

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private final AtomicInteger id = new AtomicInteger();

    private CandidateStore() {
        candidates.put(id.incrementAndGet(), new Candidate(id.get(), "Ivan", "Description for Ivan", LocalDateTime.now(), new City(0, "Казань")));
        candidates.put(id.incrementAndGet(), new Candidate(id.get(), "Aleksandr", "Description for Aleksandr", LocalDateTime.now(), new City(0, "Казань")));
        candidates.put(id.incrementAndGet(), new Candidate(id.get(), "Vladimir", "Description for Vladimir", LocalDateTime.now(), new City(0, "Казань")));
    }

    public Collection<Candidate> findAll() {
        return candidates.values();
    }

    public void add(Candidate candidate) {
        candidate.setId(id.incrementAndGet());
        candidates.putIfAbsent(candidate.getId(), candidate);
    }

    public Candidate findById(int id) {
        return candidates.get(id);
    }

    public void update(Candidate candidate) {
        candidates.replace(candidate.getId(), candidate);
    }
}
