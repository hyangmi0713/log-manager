package jp.co.canon.rss.logmanager.mapper.job;

import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobDetailDTO;
import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobStepDetailDTO;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import jp.co.canon.rss.logmanager.vo.job.JobEntity;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResRemobteJobDetailVoToDtoMapper {
    ResRemobteJobDetailVoToDtoMapper INSTANCE = Mappers.getMapper(ResRemobteJobDetailVoToDtoMapper.class);

    @Mapping(target="steps", expression = "java(mapSteps(jobEntity.getSteps()))")
    @Mapping(target="siteName", expression = "java(mapSiteName(jobEntity.getSiteVo()))")
    ResRemoteJobDetailDTO mapRemoteJobDetailVoToDto(JobEntity jobEntity);

    default String mapSiteName(SiteVo siteVo) {
        return siteVo.getCrasCompanyName() + "-" + siteVo.getCrasFabName();
    }

    default List<ResRemoteJobStepDetailDTO> mapSteps(List<StepEntity> stepEntityList) {
        List<ResRemoteJobStepDetailDTO> remoteJobStepDetailDTOS = new ArrayList<>();

        for (StepEntity stepEntity : stepEntityList) {
            stepEntity.setFileIndices(stepEntity.getFileIndices() == null ? new int[0] : stepEntity.getFileIndices());
            remoteJobStepDetailDTOS.add(ResStepDetailVoToDtoMapper.INSTANCE.toDto(stepEntity));
        }

        return remoteJobStepDetailDTOS;
    }
}