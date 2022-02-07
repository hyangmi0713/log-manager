package jp.co.canon.rss.logmanager.mapper.status;

import jp.co.canon.rss.logmanager.config.RunStep;
import jp.co.canon.rss.logmanager.dto.job.ResLocalJobListDTO;
import jp.co.canon.rss.logmanager.vo.job.HistoryEntity;
import jp.co.canon.rss.logmanager.vo.job.JobEntity;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResLocalJobDtoMapper {
    ResLocalJobDtoMapper INSTANCE = Mappers.getMapper(ResLocalJobDtoMapper.class);

    @Mapping(target="jobId", expression = "java(mapInteger(jobEntity.getJobId()))")
    @Mapping(target="companyName", expression = "java(mapString(jobEntity.getSiteVo().getCrasCompanyName()))")
    @Mapping(target="fabName", expression = "java(mapString(jobEntity.getSiteVo().getCrasFabName()))")
    @Mapping(target="stepType", expression = "java(mapString(convertStep.getStepType()))")
    @Mapping(target="status", expression = "java(mapStatus(convertStep.getHistories()))")
    @Mapping(target="error", expression = "java(mapError(convertStep.getHistories()))")
    @Mapping(target="fileIndices", expression = "java(mapIntArray(convertStep.getFileIndices()))")
    ResLocalJobListDTO mapLocalJobDto(JobEntity jobEntity, StepEntity convertStep);

    default String mapString(String name) {
        return name; }

    default Integer mapInteger(Integer name) {
        return name; }

    default int [] mapIntArray(int [] localFileId) {
        return localFileId == null ? new int[0] : localFileId; }

    default String mapStatus(List<HistoryEntity> historyEntityList) {
        String stauts = null;
        List<HistoryEntity> historyEntityListSort = historyEntitySort(historyEntityList);
        if(historyEntityListSort.size() != 0)
            stauts = historyEntityListSort.get(0).getStatus();
        else
            stauts = RunStep.STATUS_NOTBUILD;
        return stauts;
    }

    default String [] mapError(List<HistoryEntity> historyEntityList) {
        String [] error = new String[0];
        List<HistoryEntity> historyEntityListSort = historyEntitySort(historyEntityList);
        if(historyEntityListSort.size() != 0) {
            if(historyEntityListSort.get(0).getError() != null)
                error = historyEntityListSort.get(0).getError();
        }
        return error;
    }

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