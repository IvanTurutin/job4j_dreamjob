package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ThreadSafe
@Repository
public class CandidateDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(PostDBStore.class.getName());

    private static final String TABLE_NAME_CANDIDATES = "candidates";
    private static final String TABLE_NAME_CITIES = "cities";
    private static final String TRUNCATE_TABLE = String.format("TRUNCATE TABLE %s RESTART IDENTITY", TABLE_NAME_CANDIDATES);
    private static final String SELECT_STATEMENT = String.format(
        "SELECT cnd.*, "
                + "c.name as city_name "
                + "FROM %s as cnd "
                + "JOIN %s as c "
                + "ON cnd.city_id = c.id ",
        TABLE_NAME_CANDIDATES,
        TABLE_NAME_CITIES);
    private static final String FIND_ALL_STATEMENT = SELECT_STATEMENT + "ORDER BY id";
    private static final String FIND_BY_ID_STATEMENT = SELECT_STATEMENT + "WHERE cnd.id = ?";
    private static final String ADD_STATEMENT = String.format("INSERT INTO %s(name, description, date, visible, city_id) "
            + "VALUES (?, ?, ?, ?, ?)", TABLE_NAME_CANDIDATES);
    private static final String UPDATE_STATEMENT = String.format(
            "UPDATE %s "
                    + "SET name = ?, "
                    + "description = ?, "
                    + "date = ?, "
                    + "visible = ?, "
                    + "city_id = ? "
                    + "WHERE id = ?", TABLE_NAME_CANDIDATES);

    public CandidateDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Collection<Candidate> findAll() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(FIND_ALL_STATEMENT)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(createCandidate(it));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in PostDBStore", e);
        }
        return candidates;
    }

    public Candidate add(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(ADD_STATEMENT,
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
             PreparedStatement ps =  cn.prepareStatement(FIND_BY_ID_STATEMENT)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return createCandidate(it);
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in CandidateDBStore", e);
        }
        return null;
    }

    public void update(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(UPDATE_STATEMENT)
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

    private Candidate createCandidate(ResultSet it) {
        try {
            return new Candidate(
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
