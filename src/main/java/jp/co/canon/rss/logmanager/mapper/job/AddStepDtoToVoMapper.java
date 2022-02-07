package jp.co.canon.rss.logmanager.mapper.job;

import jp.co.canon.rss.logmanager.dto.job.ReqJobStepAddDTO;
import jp.co.canon.rss.logmanager.mapper.GenericMapper;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddStepDtoToVoMapper extends GenericMapper<ReqJobStepAddDTO, StepEntity> {
    AddStepDtoToVoMapper INSTANCE = Mappers.getMapper(AddStepDtoToVoMapper.class);
}