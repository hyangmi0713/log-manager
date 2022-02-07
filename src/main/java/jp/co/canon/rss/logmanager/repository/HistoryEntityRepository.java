package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.job.HistoryEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HistoryEntityRepository extends JpaRepository<HistoryEntity, Integer> {
    HistoryEntity findByHistoryId(int historyId);
    List<HistoryEntity> findByJobIdAndStepId(int jobId, int stepId);
    List<HistoryEntity> findByJobIdAndStatus(int jobId, String status, Sort sort);
    List<HistoryEntity> findByJobId(int jobId);
    List<HistoryEntity> findByEndDate(String endDate);
    List<HistoryEntity> findByStatusOrStatus(String processing, String notbuild);
    HistoryEntity findByRid(String logId);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.history set end_date = :endingTime where id = :id", nativeQuery = true)
    void updateHistoryEndingTime(@Param("id") int id, @Param("endingTime") String endingTime);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.history set status = :status where id = :id", nativeQuery = true)
    void updateHistoryStatus(@Param("id") int id, @Param("status") String status);

    @Query(value = "SELECT * " +
            "FROM log_manager.history " +
            "WHERE (:step_id, run_date) " +
            "IN (SELECT step_id, MAX(run_date) as run_date from log_manager.history GROUP BY step_id)", nativeQuery = true)
    List<HistoryEntity> selectRunDateLatestHistory(@Param("step_id") int step_id);
}
