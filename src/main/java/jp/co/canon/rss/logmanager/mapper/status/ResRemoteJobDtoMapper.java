package jp.co.canon.rss.logmanager.mapper.status;

import jp.co.canon.rss.logmanager.config.RunStep;
import jp.co.canon.rss.logmanager.dto.job.ResLastStepDTO;
import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobDTO;
import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobStepDTO;
import jp.co.canon.rss.logmanager.vo.job.HistoryEntity;
import jp.co.canon.rss.logmanager.vo.job.JobEntity;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResRemoteJobDtoMapper {
    String STATUS_FAILURE = RunStep.STATUS_FAILURE;
    String STATUS_SUCCESS = RunStep.STATUS_SUCCESS;

    ResRemoteJobDtoMapper INSTANCE = Mappers.getMapper(ResRemoteJobDtoMapper.class);

    @Mapping(target="companyName", expression = "java(mapName(jobEntity.getSiteVo().getCrasCompanyName()))")
    @Mapping(target="fabName", expression = "java(mapName(jobEntity.getSiteVo().getCrasFabName()))")
    @Mapping(target="lastFailure", expression = "java(mapLastStep(jobEntity.getHistories(), STATUS_FAILURE))")
    @Mapping(target="lastSuccess", expression = "java(mapLastStep(jobEntity.getHistories(), STATUS_SUCCESS))")
    ResRemoteJobDTO mapRemoteJobDto(JobEntity jobEntity);

    @Mapping(target="status", expression = "java(mapStatus(stepEntity.getHistories()))")
    @Mapping(target="error", expression = "java(mapError(stepEntity.getHistories()))")
    @Mapping(target="lastFailure", expression = "java(mapLastStep(stepEntity.getHistories(), STATUS_FAILURE))")
    @Mapping(target="lastSuccess", expression = "java(mapLastStep(stepEntity.getHistories(), STATUS_SUCCESS))")
    ResRemoteJobStepDTO mapRemoteJobStepDto(StepEntity stepEntity);

    @Mapping(target="error", expression = "java(mapStringArray(historyEntity.getError()))")
    @Mapping(target="manual", expression = "java(mapBoolean(historyEntity.getIsManual()))")
    @Mapping(target="date", expression = "java(mapName(historyEntity.getRunDate()))")
    @Mapping(target="stepName", expression = "java(mapName(historyEntity.getStepEntity().getStepName()))")
    ResLastStepDTO mapLastStepDto(HistoryEntity historyEntity);

    default String mapStatus(List<HistoryEntity> historyEntityList) {
        String stauts = RunStep.STATUS_NOTBUILD;
        List<HistoryEntity> historyEntityListEndDate = new ArrayList<>();
        for(HistoryEntity historyEntity : historyEntityList) {
            if(historyEntity.getEndDate() != null)
                historyEntityListEndDate.add(historyEntity);
        }
        List<HistoryEntity> historyEntityListSort = historyEntitySort(historyEntityListEndDate);
        if(historyEntityListSort.size() != 0)
            stauts = historyEntityListSort.get(0).getStatus();
        return stauts;
    }

    default String [] mapError(List<HistoryEntity> historyEntityList) {
        String [] error = new String[0];
        List<HistoryEntity> historyEntityListEndDate = new ArrayList<>();
        for(HistoryEntity historyEntity : historyEntityList) {
            if(historyEntity.getEndDate() != null)
                historyEntityListEndDate.add(historyEntity);
        }
        List<HistoryEntity> historyEntityListSort = historyEntitySort(historyEntityListEndDate);
        if(historyEntityListSort.size() != 0)
            error = historyEntityListSort.get(0).getError() == null ? new String[0] : historyEntityListSort.get(0).getError();
        return error;
    }

    default ResLastStepDTO mapLastStep(List<HistoryEntity> historyEntityList, String status) {
        ResLastStepDTO resLastStepDTO = new ResLastStepDTO();
        List<HistoryEntity> historyEntityListEndDate = new ArrayList<>();

        for(HistoryEntity historyEntity : historyEntityList) {
            if(historyEntity.getEndDate() != null)
                historyEntityListEndDate.add(historyEntity);
        }
        List<HistoryEntity> historyEntityListSort = historyEntitySort(historyEntityListEndDate);

        for(HistoryEntity historyEntity : historyEntityListSort) {
            if(historyEntity.getStatus().equals(status)) {
                resLastStepDTO.setStepType(historyEntity.getStepType())
                        .setStepId(historyEntity.getStepId())
                        .setHistoryId(historyEntity.getRid())
                        .setStepName(historyEntity.getStepEntity().getStepName())
                        .setDate(historyEntity.getEndDate())
                        .setManual(historyEntity.getIsManual())
                        .setStatus(historyEntity.getStatus())
                        .setError(historyEntity.getError()==null ? new String[0] : historyEntity.getError());
                return resLastStepDTO;
            }
        }

        return null;
    }

    default String mapName(String name) { return name; }

    default Boolean mapBoolean(Boolean isManual) { return isManual; }

    default String [] mapStringArray(String [] error) { return error==null ? new String[0] : error; }

    default List<HistoryEntity> historyEntitySort(List<HistoryEntity> historyEntityList) {
        Collections.sort(historyEntityList, new Comparator<HistoryEntity>() {
            @Override
            public  int compare(HistoryEntity o1, HistoryEntity o2) {
                return o2.getEndDate().compareTo(o1.getEndDate());
            }
        });
        return historyEntityList;
    }
}
