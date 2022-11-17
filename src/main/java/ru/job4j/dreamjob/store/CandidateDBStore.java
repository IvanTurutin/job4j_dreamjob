package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ThreadSafe
@Repository
public class CandidateDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(PostDBStore.class.getName());

    public CandidateDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Collection<Candidate> findAll() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "SELECT cnd.id as candidate_id, "
                             + "cnd.name as candidate_name, "
                             + "cnd.description as candidate_description, "
                             + "cnd.date as candidate_date, "
                             + "cnd.visible as candidate_visible, "
                             + "cnd.city_id as candidate_city_id, "
                             + "c.name as city_name "
                             + "FROM candidates as cnd "
                             + "JOIN cities as c "
                             + "ON cnd.city_id = c.id "
                             + "ORDER BY candidate_id"
             )
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(
                            new Candidate(
                                    it.getInt("candidate_id"),
                                    it.getString("candidate_name"),
                                    it.getString("candidate_description"),
                                    it.getTimestamp("candidate_date").toLocalDateTime(),
                                    it.getBoolean("candidate_visible"),
                                    new City(it.getInt("candidate_city_id"), it.getString("city_name"))
                            )
                    );
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
        return candidates;
    }

    public Candidate add(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "INSERT INTO candidates(name, description, date, visible, city_id) "
                             + "VALUES (?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(candidate.getCreate()));
            ps.setBoolean(4, candidate.isVisible());
            ps.setInt(5, candidate.getCity().getId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in CandidateDBStore", e);
        }
        return candidate;
    }

    public Candidate findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "SELECT cnd.id as candidate_id, "
                             + "cnd.name as candidate_name, "
                             + "cnd.description as candidate_description, "
                             + "cnd.date as candidate_date, "
                             + "cnd.visible as candidate_visible, "
                             + "cnd.city_id as candidate_city_id, "
                             + "c.name as city_name "
                             + "FROM candidates as cnd "
                             + "JOIN cities as c "
                             + "ON cnd.city_id = c.id "
                             + "WHERE cnd.id = ?"
             )
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Candidate(
                            it.getInt("candidate_id"),
                            it.getString("candidate_name"),
                            it.getString("candidate_description"),
                            it.getTimestamp("candidate_date").toLocalDateTime(),
                            it.getBoolean("candidate_visible"),
                            new City(it.getInt("candidate_city_id"), it.getString("city_name"))
                    );
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in CandidateDBStore", e);
        }
        return null;
    }

    public void update(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "UPDATE candidates "
                             + "SET name = ?, "
                             + "description = ?, "
                             + "date = ?, "
                             + "visible = ?, "
                             + "city_id = ? "
                             + "WHERE id = ?")
        ) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(candidate.getCreate()));
            ps.setBoolean(4, candidate.isVisible());
            ps.setInt(5, candidate.getCity().getId());
            ps.setInt(6, candidate.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
    }
}
