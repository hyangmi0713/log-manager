package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.config.RunStep;
import jp.co.canon.rss.logmanager.dto.job.*;
import jp.co.canon.rss.logmanager.dto.rulecrasdata.ResCrasDataSiteInfoDTO;
import jp.co.canon.rss.logmanager.dto.site.ResSitesNamesDTO;
import jp.co.canon.rss.logmanager.mapper.status.ResLocalJobDtoMapper;
import jp.co.canon.rss.logmanager.mapper.status.ResRemoteJobDtoMapper;
import jp.co.canon.rss.logmanager.repository.*;
import jp.co.canon.rss.logmanager.repository.crasdata.CrasDataSiteRepository;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataSiteVo;
import jp.co.canon.rss.logmanager.vo.job.HistoryEntity;
import jp.co.canon.rss.logmanager.vo.job.JobEntity;
import jp.co.canon.rss.logmanager.vo.job.LocalJobFileEntity;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service()
public class StatusService {
    SiteRepository siteRepositoryService;
    CrasDataSiteRepository crasDataSiteRepository;
    JobRepository jobRepository;
    StepRepository stepRepository;
    SiteService siteService;
    HistoryEntityRepository historyEntityRepository;
    LocalJobFileEntityRepository localJobFileEntityRepository;

    public StatusService(SiteRepository siteRepositoryService,
                         CrasDataSiteRepository crasDataSiteRepository,
                         JobRepository jobRepository, StepRepository stepRepository,
                         SiteService siteService, HistoryEntityRepository historyEntityRepository,
                         LocalJobFileEntityRepository localJobFileEntityRepository) {
        this.siteRepositoryService = siteRepositoryService;
        this.crasDataSiteRepository = crasDataSiteRepository;
        this.jobRepository = jobRepository;
        this.stepRepository = stepRepository;
        this.siteService = siteService;
        this.historyEntityRepository = historyEntityRepository;
        this.localJobFileEntityRepository = localJobFileEntityRepository;
    }

    public List<ResRemoteJobDTO> getRemoteJobs() throws Exception {
        try {
            List<JobEntity> jobEntityList = Optional
                    .ofNullable(jobRepository.findByType(RunStep.TYPE_REMOTE, Sort.by(Sort.Direction.DESC, "jobId")))
                    .orElse(Collections.emptyList());

            List<ResRemoteJobDTO> resultRemoteList = new ArrayList<>();
            for(JobEntity jobEntity : jobEntityList)
                resultRemoteList.add(ResRemoteJobDtoMapper.INSTANCE.mapRemoteJobDto(jobEntity));

            return resultRemoteList;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ResRemoteJobStepDTO> getRemoteJobSteps(int jobId) throws Exception {
        try {
            List<StepEntity> jobEntityList = Optional
                    .ofNullable(stepRepository.findByJobId(jobId, Sort.by(Sort.Direction.DESC, "stepId")))
                    .orElse(Collections.emptyList());

            List<ResRemoteJobStepDTO> resultRemoteStepList = new ArrayList<>();
            for(StepEntity stepEntity : jobEntityList) {
                resultRemoteStepList.add(ResRemoteJobDtoMapper.INSTANCE.mapRemoteJobStepDto(stepEntity));
            }

            return resultRemoteStepList;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ResLastStepDTO> getRemoteJobBuildQueue(int jobId) throws Exception {
        try {
            JobEntity jobEntity = jobRepository.findByJobId(jobId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            // 작업이 stop 상태이면 빈배열 리턴
            if(jobEntity.getStop() == true)
                return new ArrayList<>();

            // 최종 리턴값
            List<ResLastStepDTO> resultRemoteStepList = new ArrayList<>();

            // 모든 step 값 취득
            List<StepEntity> stepEntityList =
                    stepRepository.findByJobId(jobId, Sort.by(Sort.Direction.DESC, "stepId"));

            // 하루에 실행해야 하는 갯수 확인
            int totalCountOfDay = 0;
            for(StepEntity stepEntity : stepEntityList) {
                if(stepEntity.getMode().equals(RunStep.MODE_CYCLE))
                    totalCountOfDay += 24 / stepEntity.getPeriod();
                else if(stepEntity.getMode().equals(RunStep.MODE_TIME))
                    totalCountOfDay += stepEntity.getTime().length;
            }

            for(StepEntity stepEntity : stepEntityList) {
                LocalDateTime latestDate = null;

                for (int idx = 0; idx < RunStep.JOB_TIMELINE / totalCountOfDay; idx++) {
                    if (stepEntity.getMode().equals(RunStep.MODE_CYCLE)) {
                        for(int i = 0; i < 24 / stepEntity.getPeriod(); i++) {
                            ResLastStepDTO resTimeLineDTONone = new ResLastStepDTO();

                            String dateCycle = null;

                            if (idx == 0 && i == 0) {
                                List<HistoryEntity> historyEntity = historyEntityRepository.selectRunDateLatestHistory(stepEntity.getStepId());
                                latestDate = LocalDateTime.parse(historyEntity.get(0).getRunDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            }

                            if (stepEntity.getCycle().equals(RunStep.CYCLE_DAY))
                                latestDate = latestDate.plusDays(stepEntity.getPeriod());
                            else if (stepEntity.getCycle().equals(RunStep.CYCLE_HOUR))
                                latestDate = latestDate.plusHours(stepEntity.getPeriod());
                            else if (stepEntity.getCycle().equals(RunStep.CYCLE_MINUTE))
                                latestDate = latestDate.plusMinutes(stepEntity.getPeriod());

                            dateCycle = latestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                            resTimeLineDTONone.setStepType(stepEntity.getStepType())
                                    .setHistoryId("")
                                    .setStepName(stepEntity.getStepName())
                                    .setDate(dateCycle)
                                    .setManual(false)
                                    .setStatus(RunStep.STATUS_NOTBUILD)
                                    .setError(new String[0]);
                            resultRemoteStepList.add(resTimeLineDTONone);
                        }
                    } else if (stepEntity.getMode().equals(RunStep.MODE_TIME)) {
                        for(String settingTime : stepEntity.getTime()) {
                            ResLastStepDTO resTimeLineDTONone = new ResLastStepDTO();
                            String dateTime = null;
                            if(idx==0) {
                                String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd "));
                                if (LocalDateTime.parse(today+settingTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                        .isAfter(LocalDateTime.now()))
                                    dateTime = LocalDateTime.now().plusDays(idx).format(DateTimeFormatter.ofPattern("yyyy-MM-dd ")) + settingTime + ":00";
                            }
                            else
                                dateTime = LocalDateTime.now().plusDays(idx).format(DateTimeFormatter.ofPattern("yyyy-MM-dd ")) + settingTime + ":00";

                            if(dateTime != null) {
                                resTimeLineDTONone.setStepType(stepEntity.getStepType())
                                        .setHistoryId("")
                                        .setStepName(stepEntity.getStepName())
                                        .setDate(dateTime)
                                        .setManual(false)
                                        .setStatus(RunStep.STATUS_NOTBUILD)
                                        .setError(new String[0]);
                                resultRemoteStepList.add(resTimeLineDTONone);
                            }
                        }
                    }
                }
            }

            // resultRemoteStepList 시간순으로 소트
            Collections.sort(resultRemoteStepList, new Comparator<ResLastStepDTO>() {
                @Override
                public  int compare(ResLastStepDTO o1, ResLastStepDTO o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });

            return resultRemoteStepList;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ResLastStepDTO> getRemoteJobBuildExecutor(int jobId) throws Exception {
        try {
            JobEntity jobEntity = jobRepository.findByJobId(jobId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            if(jobEntity.getStop() == true)
                return new ArrayList<>();

            List<HistoryEntity> historyEntityList = Optional
                    .ofNullable(historyEntityRepository.findByJobIdAndStatus(
                            jobId, RunStep.STATUS_PROCESSING, Sort.by(Sort.Direction.DESC, "runDate")))
                    .orElse(Collections.emptyList());

            List<ResLastStepDTO> resultRemoteStepList = new ArrayList<>();
            for(HistoryEntity historyEntity : historyEntityList) {
                resultRemoteStepList.add(ResRemoteJobDtoMapper.INSTANCE.mapLastStepDto(historyEntity));
            }

            return resultRemoteStepList;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ResLocalJobListDTO> localJobListDTOS() {
        try {
            List<JobEntity> jobEntityList = Optional
                    .ofNullable(jobRepository.findByType(RunStep.TYPE_LOCAL, Sort.by(Sort.Direction.DESC, "jobId")))
                    .orElse(Collections.emptyList());

            List<ResLocalJobListDTO> resultLocalList = new ArrayList<>();
            for(JobEntity jobEntity : jobEntityList) {
                List<String> fileNames = new ArrayList<>();
                for(StepEntity stepEntity : jobEntity.getSteps()) {
                    if(stepEntity.getStepType().equals(RunStep.STEPTYPE_CONVERT)) {
                        for(int fileId : stepEntity.getFileIndices()) {
                            LocalJobFileEntity localJobFileEntity = localJobFileEntityRepository.findByFileId(fileId);
                            fileNames.add(localJobFileEntity.getOriginalFileName());
                        }
                        ResLocalJobListDTO resLocalJobListDTO = ResLocalJobDtoMapper.INSTANCE.mapLocalJobDto(jobEntity, stepEntity);
                        resLocalJobListDTO.setFileOriginalNames(fileNames.toArray(new String[fileNames.size()]));
                        resLocalJobListDTO.setStepId(stepEntity.getStepId());

                        String historyId = null;
                        for (HistoryEntity historyEntity : stepEntity.getHistories()) {
                            if(historyEntity.getStepType().equals(RunStep.STEPTYPE_CONVERT))
                                historyId = historyEntity.getRid();
                        }
                        resLocalJobListDTO.setHistoryId(historyId);
                        resultLocalList.add(resLocalJobListDTO);
                    }
                }
            }

            return resultLocalList;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ResSitesNamesDTO> getSitesNamesList(Boolean notAdded) {
        List<ResSitesNamesDTO> resultSitesNamesList = Optional
                .ofNullable(siteRepositoryService.findBy())
                .orElse(Collections.emptyList());
        if(resultSitesNamesList==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Collections.sort(resultSitesNamesList);

        if(notAdded) {
            List<JobEntity> jobEntityList = Optional
                    .ofNullable(jobRepository.findByType(RunStep.TYPE_REMOTE, Sort.by(Sort.Direction.DESC, "jobId")))
                    .orElse(Collections.emptyList());

            List<ResSitesNamesDTO> jobSiteNamesList = new ArrayList<>();

            for (JobEntity jobEntity : jobEntityList) {
                ResSitesNamesDTO sitesNames = new ResSitesNamesDTO(jobEntity.getSiteId(),
                        jobEntity.getSiteVo().getCrasCompanyName(), jobEntity.getSiteVo().getCrasFabName());
                jobSiteNamesList.add(sitesNames);
            }

            resultSitesNamesList.removeAll(jobSiteNamesList);
        }
        return resultSitesNamesList;
    }

    public List<ResCrasDataSiteInfoDTO> getCrasDataSiteInfo() throws Exception {
        try {
            List<CrasDataSiteVo> crasDataSiteVoList = Optional
                    .ofNullable(crasDataSiteRepository.findAll())
                    .orElse(Collections.emptyList());
            List<SiteVo> siteVoList = Optional
                    .ofNullable(siteRepositoryService.findAll())
                    .orElse(Collections.emptyList());

            if (crasDataSiteVoList == null || siteVoList == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            List<ResCrasDataSiteInfoDTO> resCrasDataSiteInfoDTOList = new ArrayList<>();

            for (SiteVo siteVo : siteVoList) {
                int flag = 0;
                for (CrasDataSiteVo crasDataSiteVo : crasDataSiteVoList) {
                    if (siteVo.getSiteId() == crasDataSiteVo.getCrasDataSiteVo().getSiteId()) {
                        flag = 1;
                        break;
                    }
                }

                if (flag == 0) {
                    ResCrasDataSiteInfoDTO resCrasDataSiteInfoDTO = new ResCrasDataSiteInfoDTO()
                            .setSiteId(siteVo.getSiteId())
                            .setName(siteVo.getCrasCompanyName()+"-"+siteVo.getCrasFabName());
                    resCrasDataSiteInfoDTOList.add(resCrasDataSiteInfoDTO);
                }
            }

            return resCrasDataSiteInfoDTOList;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResRemoteJobStatusDTO getStatusRemoteJob(int remoteJobId) {
        try {
            JobEntity jobEntity = jobRepository.findByJobId(remoteJobId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            ResRemoteJobStatusDTO resRemoteJobStatusDTO = new ResRemoteJobStatusDTO()
                    .setStop(jobEntity.getStop());

            return resRemoteJobStatusDTO;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ResRegisteredJobDTO> getRegisteredJob(int siteId) {
        try {
            List<ResRegisteredJobDTO> resRegisteredJobDtoList = new ArrayList<>();
            List<JobEntity> jobEntityList = jobRepository.findBySiteIdAndType(siteId, RunStep.TYPE_REMOTE);

            for(JobEntity jobEntity : jobEntityList) {
                ResRegisteredJobDTO resRegisteredJobDTO = new ResRegisteredJobDTO()
                        .setCompanyName(jobEntity.getSiteVo().getCrasCompanyName())
                        .setFabName(jobEntity.getSiteVo().getCrasFabName())
                        .setJobId(jobEntity.getJobId())
                        .setJobName(jobEntity.getJobName())
                        .setSiteId(jobEntity.getSiteId())
                        .setStop(jobEntity.getStop());
                resRegisteredJobDtoList.add(resRegisteredJobDTO);
            }

            return resRegisteredJobDtoList;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}