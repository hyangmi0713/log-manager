package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.job.LocalJobFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalJobFileEntityRepository extends JpaRepository<LocalJobFileEntity, Integer> {
    LocalJobFileEntity findByFileId(int fileId);
}
