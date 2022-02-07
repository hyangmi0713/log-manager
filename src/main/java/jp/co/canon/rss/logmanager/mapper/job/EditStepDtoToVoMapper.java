package jp.co.canon.rss.logmanager.mapper.job;

import jp.co.canon.rss.logmanager.dto.job.ReqJobStepAddDTO;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EditStepDtoToVoMapper {
    EditStepDtoToVoMapper INSTANCE = Mappers.getMapper(EditStepDtoToVoMapper.class);

    StepEntity mapReqStepEditDtoToVo(ReqJobStepAddDTO reqJobStepAddDTO, @MappingTarget StepEntity step);
}