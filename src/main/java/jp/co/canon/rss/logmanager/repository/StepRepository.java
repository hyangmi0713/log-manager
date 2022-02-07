package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobStepEnableDTO;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepRepository extends JpaRepository<StepEntity, Integer>  {
    StepEntity findByStepId(int jobId);
    List<StepEntity> findByJobId(int jobId, Sort sort);
    List<ResRemoteJobStepEnableDTO> findByJobIdAndEnable(int jobId, Boolean enable);
    StepEntity findByJobIdAndStepType(int jobId, String stepType);
    List<StepEntity> findByPreStep(String stepUuid);

    void deleteByJobId(int jobId);
}
