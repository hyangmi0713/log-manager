package jp.co.canon.rss.logmanager.mapper.history;

import jp.co.canon.rss.logmanager.dto.history.ResHistoryDTO;
import jp.co.canon.rss.logmanager.vo.job.HistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HistoryVoDTOMapper {
    HistoryVoDTOMapper INSTANCE = Mappers.getMapper(HistoryVoDTOMapper.class);

    @Mapping(target="id", expression = "java(mapId(historyEntity))")
    @Mapping(target="name", expression = "java(mapName(historyEntity))")
    ResHistoryDTO mapResHistoryDTO(HistoryEntity historyEntity);

    default String mapId(HistoryEntity historyEntity) { return historyEntity.getRid(); }
    default String mapName(HistoryEntity historyEntity) { return historyEntity.getRunDate(); }
}
