package jp.co.canon.rss.logmanager.mapper.job;

import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobStepDetailDTO;
import jp.co.canon.rss.logmanager.mapper.GenericMapper;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResStepDetailVoToDtoMapper extends GenericMapper<ResRemoteJobStepDetailDTO, StepEntity> {
    ResStepDetailVoToDtoMapper INSTANCE = Mappers.getMapper(ResStepDetailVoToDtoMapper.class);

}