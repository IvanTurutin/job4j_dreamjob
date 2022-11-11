package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class CityStore {

    private final Map<Integer, City> cities = new ConcurrentHashMap<>();
    private final AtomicInteger id = new AtomicInteger();

    private CityStore() {
        cities.put(id.incrementAndGet(), new City(id.get(), "Москва"));
        cities.put(id.incrementAndGet(), new City(id.get(), "С-Пб"));
        cities.put(id.incrementAndGet(), new City(id.get(), "Самара"));
    }

    public List<City> getAllCities() {
        return new ArrayList<>(cities.values());
    }

    public City findById(int id) {
        return cities.get(id);
    }

}
