package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.job.JobEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Integer>  {
    Optional<JobEntity> findByJobId(int jobId);
    List<JobEntity> findByType(String type, Sort sort);
    List<JobEntity> findBySiteIdAndType(int siteId, String type);
}
