package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.store.CityDBStore;
import ru.job4j.dreamjob.store.CityStore;

import java.util.List;

@ThreadSafe
@Service
public class CityService {

    private final CityDBStore cityStore;

    public CityService(CityDBStore cityStore) {
        this.cityStore = cityStore;
    }

    public List<City> getAllCities() {
        return cityStore.getAllCities();
    }

    public City findById(int id) {
        return cityStore.findById(id);
    }

}
