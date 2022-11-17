package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@ThreadSafe
@Repository
public class CityDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(CityDBStore.class.getName());

    private CityDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "SELECT * FROM cities ORDER BY id"
             )
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    cities.add(new City(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in CityDBStore", e);
        }
        return cities;    }

    public City findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "SELECT * FROM cities WHERE id = ?"
             )
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new City(
                            it.getInt("id"),
                            it.getString("name")

                    );
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in CityDBStore", e);
        }
        return null;
    }
}

