package platform.code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CodeService {

    private final CodeRepository codeRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public Code findCodeById(UUID id) {
        Optional<Code> codeOptional = codeRepository.findById(id);
        if (codeOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Code with id: " + id + " does not exists");
        }
        Code code = codeOptional.get();
        if (code.getRestriction() == Code.Restriction.BOTH || code.getRestriction() == Code.Restriction.VIEWS) {
            updateViews(code);
        }
        if (code.getRestriction() == Code.Restriction.BOTH || code.getRestriction() == Code.Restriction.TIME) {
            updateExpirationTime(code);
        }
        return code;
    }

    public void updateViews(Code code) {
        code.setViews(code.getViews() - 1); // update views in object
        // update in database
        if (code.getViews() == 0) {
            codeRepository.delete(code);
        } else {
            codeRepository.save(code);
        }
    }

    public void updateExpirationTime(Code code) {
        Instant now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        Instant expirationDate = Instant.ofEpochSecond(code.getExpirationTime());
        long diffSeconds = Duration.between(now, expirationDate).getSeconds();
        code.setExpirationTime(diffSeconds);
    }
    public Code addNewCode(Code code) {
        code.setLoadedAt(LocalDateTime.now());
        if (code.getExpirationTime() != 0) {
            long expirationTime = LocalDateTime.now()
                    .plusSeconds(code.getExpirationTime())
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();

            code.setExpirationTime(expirationTime);
        }

        return codeRepository.save(code);
    }
    public List<Code> getLatestCodeByLimit(int limit) {
        String query = """
                SELECT c FROM Code c
                WHERE c.restriction = :restriction
                ORDER BY loadedAt DESC
                """;
        return entityManager
                .createQuery(query, Code.class)
                .setParameter("restriction", Code.Restriction.NON)
                .setMaxResults(limit)
                .getResultList();
    }

    @Transactional
    @Scheduled(fixedRate = 1000)
    public void deleteExpiredRows() {

        String queryDelete = """
                DELETE FROM Code c
                WHERE c.expirationTime <= :current
                AND (c.restriction = :both OR c.restriction = :time)
                """;

        Query query = entityManager.createQuery(queryDelete);
        query.setParameter("both", Code.Restriction.BOTH);
        query.setParameter("time", Code.Restriction.TIME);
        query.setParameter("current", LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        query.executeUpdate();
    }


}
