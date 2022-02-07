package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.LogDownloadStatusVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LogDownloadStatusRepository extends JpaRepository<LogDownloadStatusVo, Integer> {
    List<LogDownloadStatusVo> findBySiteId(int siteId);
    LogDownloadStatusVo findByClientId(String clientId);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.log_download_status set rid_cras_search = :rid_cras_search where id = :id", nativeQuery = true)
    void updateRidCrasSearch(@Param("id") int id, @Param("rid_cras_search") String rid_cras_search);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.log_download_status set rid_cras_download = :rid_cras_download where id = :id", nativeQuery = true)
    void updateRidCrasDownload(@Param("id") int id, @Param("rid_cras_download") String rid_cras_download);

    @Transactional
    @Modifying
    @Query(value="UPDATE log_manager.log_download_status set client_id = :client_id where id = :id", nativeQuery = true)
    void updateClientId(@Param("id") int id, @Param("client_id") String client_id);
}
