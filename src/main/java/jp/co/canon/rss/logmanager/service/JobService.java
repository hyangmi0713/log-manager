package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.config.RunStep;
import jp.co.canon.rss.logmanager.dto.address.AddressBookDTO;
import jp.co.canon.rss.logmanager.dto.job.*;
import jp.co.canon.rss.logmanager.dto.rulecrasdata.ResCrasDataDTO;
import jp.co.canon.rss.logmanager.dto.site.ResPlanDTO;
import jp.co.canon.rss.logmanager.dto.site.ResRCPlanDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.mapper.job.AddStepDtoToVoMapper;
import jp.co.canon.rss.logmanager.mapper.job.EditStepDtoToVoMapper;
import jp.co.canon.rss.logmanager.mapper.job.ResRemobteJobDetailVoToDtoMapper;
import jp.co.canon.rss.logmanager.mapper.job.ResStepDetailVoToDtoMapper;
import jp.co.canon.rss.logmanager.repository.*;
import jp.co.canon.rss.logmanager.repository.crasdata.CrasItemMasterJobRepository;
import jp.co.canon.rss.logmanager.repository.crasdata.CrasItemMasterRepository;
import jp.co.canon.rss.logmanager.scheduler.*;
import jp.co.canon.rss.logmanager.system.ClientManageService;
import jp.co.canon.rss.logmanager.util.AscendingObj;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.util.ManualJobThread;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import jp.co.canon.rss.logmanager.vo.address.AddressBookEntity;
import jp.co.canon.rss.logmanager.vo.address.GroupBookEntity;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterVo;
import jp.co.canon.rss.logmanager.vo.job.JobEntity;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service()
public class JobService {
	private final static String PLAN_TYPE_FTP = "ftp";
	private final static String PLAN_TYPE_VFTP_COMPAT = "vftp_compat";
	private final static String PLAN_TYPE_VFTP_SSS = "vftp_sss";

	@Autowired
	private ThreadPoolTaskScheduler scheduler;
	@Value("${logmonitor.logging.root}")
	private String loggingPath;

	@Autowired
	private Job job;
	@Autowired
	private Report report;
	@Autowired
	private Custom custom;

	private Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

	JobAddressBookRepository jobAddressBookRepository;
	JobGroupBookRepository jobGroupBookRepository;
	AddressBookRepository addressBookRepository;
	GroupBookRepository groupBookRepository;
	CrasItemMasterRepository crasItemMasterRepository;
	CrasItemMasterJobRepository crasItemMasterJobRepository;
	ClientManageService clientManageService;
	SiteRepository siteRepository;
	StepRepository stepRepository;
	JobRepository jobRepository;

	public JobService(JobAddressBookRepository jobAddressBookRepository, JobGroupBookRepository jobGroupBookRepository,
					  AddressBookRepository addressBookRepository, GroupBookRepository groupBookRepository,
					  ClientManageService clientManageService,
					  CrasItemMasterRepository crasItemMasterRepository,CrasItemMasterJobRepository crasItemMasterJobRepository,
					  SiteRepository siteRepository, StepRepository stepRepository, JobRepository jobRepository) {
		this.jobAddressBookRepository = jobAddressBookRepository;
		this.jobGroupBookRepository = jobGroupBookRepository;
		this.addressBookRepository = addressBookRepository;
		this.groupBookRepository = groupBookRepository;
		this.crasItemMasterRepository = crasItemMasterRepository;
		this.crasItemMasterJobRepository = crasItemMasterJobRepository;
		this.clientManageService = clientManageService;
		this.siteRepository = siteRepository;
		this.stepRepository = stepRepository;
		this.jobRepository = jobRepository;
	}

	public ResRemoteJobDetailDTO getRemoteJobDetail(int jobId) throws Exception {
		try {
			JobEntity jobEntity = jobRepository.findByJobId(jobId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

			ResRemoteJobDetailDTO resRemoteJobDetailDTO = ResRemobteJobDetailVoToDtoMapper.INSTANCE.mapRemoteJobDetailVoToDto(jobEntity);

			for(StepEntity stepEntity : jobEntity.getSteps()) {
				List<AddressBookDTO> addressBookDTOList = emailIdToInfo(stepEntity.getEmailBookIds());
				List<AddressBookDTO> groupBookDTOList = groupIdToInfo(stepEntity.getGroupBookIds());
				List<ResCrasDataDTO> resCrasDataDTOList = crasJudgeIdToInfo(stepEntity.getSelectJudgeRuleIds());

				for (ResRemoteJobStepDetailDTO stepDto : resRemoteJobDetailDTO.getSteps()) {
					if(stepDto.getStepId() == stepEntity.getStepId()) {
						stepDto.setEmailBook(addressBookDTOList);
						stepDto.setGroupBook(groupBookDTOList);
						stepDto.setSelectJudgeRules(resCrasDataDTOList);
					}
				}
			}
			return resRemoteJobDetailDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResRemoteJobStepDetailDTO getRemoteJobStep(int stepId) throws Exception {
		try {
			StepEntity stepEntity = stepRepository.findById(stepId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			stepEntity.setFileIndices(stepEntity.getFileIndices() == null ? new int[0] : stepEntity.getFileIndices());
			ResRemoteJobStepDetailDTO resRemoteJobStepDetailDTO =
					ResStepDetailVoToDtoMapper.INSTANCE.toDto(stepEntity);

			List<AddressBookDTO> addressBookDTOList = emailIdToInfo(stepEntity.getEmailBookIds());
			List<AddressBookDTO> groupBookDTOList = groupIdToInfo(stepEntity.getGroupBookIds());
			List<ResCrasDataDTO> resCrasDataDTOList = crasJudgeIdToInfo(stepEntity.getSelectJudgeRuleIds());

			resRemoteJobStepDetailDTO
					.setEmailBook(addressBookDTOList)
					.setGroupBook(groupBookDTOList)
					.setSelectJudgeRules(resCrasDataDTOList);

			return resRemoteJobStepDetailDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<AddressBookDTO> emailIdToInfo(int [] emailIds) {
		List<AddressBookDTO> addressBookDTOList = new ArrayList<>();
		for(int id : emailIds) {
			Optional<AddressBookEntity> addressBook = addressBookRepository.findById((long) id);
			if(addressBook.isPresent()) {
				AddressBookDTO addressBookDTO = new AddressBookDTO(
						addressBook.get().getId(),
						addressBook.get().getName(),
						addressBook.get().getEmail(),
						false);
				addressBookDTOList.add(addressBookDTO);
			}
		}
		Collections.sort(addressBookDTOList, new AscendingObj());
		return addressBookDTOList;
	}

	public List<AddressBookDTO> groupIdToInfo(int [] groupIds) {
		List<AddressBookDTO> groupBookDTOList = new ArrayList<>();
		for(int id : groupIds) {
			Optional<GroupBookEntity> groupBook = groupBookRepository.findById((long) id);
			if(groupBook.isPresent()) {
				AddressBookDTO groupBookDTO = new AddressBookDTO(
						groupBook.get().getGid(),
						groupBook.get().getName(),
						"",
						true);
				groupBookDTOList.add(groupBookDTO);
			}
		}
		Collections.sort(groupBookDTOList, new AscendingObj());
		return groupBookDTOList;
	}

	public List<ResCrasDataDTO> crasJudgeIdToInfo(int [] crasIds) {
		List<ResCrasDataDTO> resCrasDataDTOList = new ArrayList<>();
		for(int id : crasIds) {
			Optional<CrasItemMasterVo> crasItemMasterVo = crasItemMasterRepository.findById(id);
			if(crasItemMasterVo.isPresent()) {
				ResCrasDataDTO resCrasDataDTO = new ResCrasDataDTO()
						.setItemId(crasItemMasterVo.get().getItemId())
						.setItemName(crasItemMasterVo.get().getItemName())
						.setEnable(crasItemMasterVo.get().getEnable());
				resCrasDataDTOList.add(resCrasDataDTO);
			}
		}
		return resCrasDataDTOList;
	}

	public List<ResRemoteJobStepEnableDTO> getRemoteJobStepEnable(int jobId) throws Exception {
		try {
			List<ResRemoteJobStepEnableDTO> resRemoteJobStepEnableDTO = stepRepository.findByJobIdAndEnable(jobId, true);
			return resRemoteJobStepEnableDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResJobIdDTO addRemoteJob(ReqRemoteJobAddDTO resRemoteJobDetailDTO) {
		int jobId = 0;
		int result = 0;
		try {
			JobEntity jobEntity = new JobEntity()
					.setJobName(resRemoteJobDetailDTO.getJobName())
					.setType(RunStep.TYPE_REMOTE)
					.setStop(true)
					.setRegisteredDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
					.setPlanIds(resRemoteJobDetailDTO.getPlanIds())
					.setSiteId(resRemoteJobDetailDTO.getSiteId())
					.setSiteVo(siteRepository.findBySiteId(resRemoteJobDetailDTO.getSiteId()));

			jobId = jobRepository.save(jobEntity).getJobId();

			ResJobIdDTO resJobIdDTO = new ResJobIdDTO()
					.setJobId(jobId);

			List<StepEntity> stepEntityList = new ArrayList<>();

			for(ReqJobStepAddDTO stepAddDTO : resRemoteJobDetailDTO.getSteps()) {
				StepEntity stepEntity = AddStepDtoToVoMapper.INSTANCE.toEntity(stepAddDTO);
				stepEntity.setJobId(jobId)
						.setJobEntity(jobEntity);

				stepEntityList.add(stepEntity);

				List<String> cron = new ArrayList<>();
				for(String time : stepAddDTO.getTime())
					cron.add(String.format("00 %s %s * * *", time.split(":")[1], time.split(":")[0]));
				stepEntity.setCron(cron.toArray(new String[cron.size()]));

				stepRepository.save(stepEntity);
			}
			jobEntity.setSteps(stepEntityList);
			result = manageJobOnCras(RunStep.MANAGE_CRAS_POST, jobEntity);

			return resJobIdDTO;
		} catch (Exception e) {
			log.error(e.toString());
			if(jobId != 0 || result != 200) {
				jobRepository.deleteById(jobId);
				stepRepository.deleteByJobId(jobId);
			}
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
	}

	public int manageJobOnCras(String flag, JobEntity jobEntity) {
		String crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_JOB,
				jobEntity.getSiteVo().getCrasAddress(), jobEntity.getSiteVo().getCrasPort());

		HttpHeaders headers = new HttpHeaders();
		HttpMethod httpMethod = null;
		HttpEntity<Object> requestAddJob = null;
		CallRestAPI callRestAPI = new CallRestAPI();

		if(flag.equals(RunStep.MANAGE_CRAS_POST) || flag.equals(RunStep.MANAGE_CRAS_PATCH)) {
			if (flag.equals(RunStep.MANAGE_CRAS_POST))
				httpMethod = HttpMethod.POST;
			else if (flag.equals(RunStep.MANAGE_CRAS_PATCH))
				httpMethod = HttpMethod.PATCH;

			headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
			headers.set(ReqURLController.JOB_CLIENT_ID, jobEntity.getType() + "_" + String.format("%06d", jobEntity.getJobId()));

			ReqAddJobDTO reqAddJobDTO = new ReqAddJobDTO();
			List<Map<String, String>> addJobDto = new ArrayList<>();

			for(StepEntity stepEntity : jobEntity.getSteps()) {
				if(stepEntity.getStepType().equals(RunStep.STEPTYPE_CUSTOM)) {
					Map<String, String> customStep = new HashMap<>();
					customStep.put("step", stepEntity.getStepType() + "_" + String.format("%06d", stepEntity.getStepId()));
					customStep.put("script_type", stepEntity.getScriptType());
					customStep.put("script", stepEntity.getScript());
					addJobDto.add(customStep);
				}
				else {
					Map<String, String> jobReportStep = new HashMap<>();
					jobReportStep.put("step", stepEntity.getStepType() + "_" + String.format("%06d", stepEntity.getStepId()));
					addJobDto.add(jobReportStep);
				}
			}

			reqAddJobDTO.setSteps(addJobDto);
			requestAddJob = new HttpEntity<>(reqAddJobDTO, headers);
		}
		else if(flag.equals(RunStep.MANAGE_CRAS_DELETE)) {
			httpMethod = HttpMethod.DELETE;
			headers.set(ReqURLController.JOB_CLIENT_ID, jobEntity.getType() + "_" + String.format("%06d", jobEntity.getJobId()));
			requestAddJob = new HttpEntity<>("", headers);
		}

		ResponseEntity<?> response = callRestAPI.exchange(
				crasServer + ReqURLController.API_POST_MANAGE_JOB,
				httpMethod,
				requestAddJob,
				Object.class);

		log.info(response.toString());

		return response.getStatusCodeValue();
	}

	public void deleteRemoteJobStep(int stepId) {
		try {
			StepEntity delStep = stepRepository.findById(stepId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			stepRepository.delete(delStep);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void deleteJob(int jobId) {
		try {
			JobEntity delJob = jobRepository.findById(jobId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			jobRepository.delete(delJob);

			manageJobOnCras(RunStep.MANAGE_CRAS_DELETE, delJob);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResJobIdDTO editRemoteJob(int jobId, ReqRemoteJobAddDTO reqRemoteJobAddDTO) {
		try {
			JobEntity jobEntity = jobRepository.findByJobId(jobId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

			jobEntity.setJobName(reqRemoteJobAddDTO.getJobName())
					.setPlanIds(reqRemoteJobAddDTO.getPlanIds());

			// change old
			for(ReqJobStepAddDTO reqDto : reqRemoteJobAddDTO.getSteps()) {
				int idx = 0;
				for(StepEntity step : jobEntity.getSteps()) {
					if(step.getStepId() == reqDto.getStepId())
						jobEntity.getSteps().set(idx, EditStepDtoToVoMapper.INSTANCE.mapReqStepEditDtoToVo(reqDto, step));
					idx++;
				}
			}

			// delete old
			List<StepEntity> delList = new ArrayList<>();
			for(StepEntity step : jobEntity.getSteps()) {
				Boolean del = true;
				for(ReqJobStepAddDTO reqDto : reqRemoteJobAddDTO.getSteps()) {
					if(step.getStepId() == reqDto.getStepId()) {
						del = false;
						break;
					}
				}
				if(del.equals(true))
					delList.add(step);
			}
			jobEntity.getSteps().removeAll(delList);

			// add new
			for(ReqJobStepAddDTO reqDto : reqRemoteJobAddDTO.getSteps()) {
				List<String> cron = new ArrayList<>();
				for(String time : reqDto.getTime())
					cron.add(String.format("00 %s %s * * *", time.split(":")[1], time.split(":")[0]));
				StepEntity stepAdd = new StepEntity()
						.setJobId(jobId)
						.setCron(cron.toArray(new String[cron.size()]))
						.setJobEntity(jobEntity);

				if(reqDto.getStepId() == null)
					jobEntity.getSteps().add(jobEntity.getSteps().size(),
							EditStepDtoToVoMapper.INSTANCE.mapReqStepEditDtoToVo(reqDto, stepAdd));
			}

			ResJobIdDTO resJobIdDTO = new ResJobIdDTO()
					.setJobId(jobRepository.save(jobEntity).getJobId());

			manageJobOnCras(RunStep.MANAGE_CRAS_PATCH, jobEntity);

			return resJobIdDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResJobIdDTO runStopRemoteJob(int remoteJobId, String flag) {
		try {
			ResJobIdDTO resJobIdDTO = new ResJobIdDTO();
			JobEntity jobEntity = jobRepository.findByJobId(remoteJobId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

			Scheduler jobScheduler = new Scheduler(job, scheduler, scheduledTasks);
			Scheduler reportScheduler = new Scheduler(report, scheduler, scheduledTasks);
			Scheduler customScheduler = new Scheduler(custom, scheduler, scheduledTasks);

			if (flag.equals("run")) {
				jobEntity.setStop(Boolean.FALSE);

				for(StepEntity stepEntity : jobEntity.getSteps()) {
					if(stepEntity.getEnable() == true) {
						switch (stepEntity.getStepType()) {
							case (RunStep.STEPTYPE_COLLECT):
							case (RunStep.STEPTYPE_CONVERT):
							case (RunStep.STEPTYPE_PURGE):
								jobScheduler.startScheduler(stepEntity);
								break;
							case (RunStep.STEPTYPE_SUMMARY):
							case (RunStep.STEPTYPE_CRAS):
							case (RunStep.STEPTYPE_VERSION):
								reportScheduler.startScheduler(stepEntity);
								break;
							case (RunStep.STEPTYPE_CUSTOM):
								customScheduler.startScheduler(stepEntity);
								break;
						}
					}
				}
			} else if (flag.equals("stop")) {
				jobEntity.setStop(Boolean.TRUE);
				jobScheduler.stopScheduler(jobEntity);
				reportScheduler.stopScheduler(jobEntity);
				customScheduler.stopScheduler(jobEntity);
			}
			resJobIdDTO.setJobId(jobRepository.save(jobEntity).getJobId());
			return resJobIdDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResJobIdDTO addLocalJob(ReqLocalJobAddDTO reqLocalJobAddDTO) {
		try {
			JobEntity jobEntity = new JobEntity()
					.setJobName(RunStep.TYPE_LOCAL+"_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
					.setType(RunStep.TYPE_LOCAL)
					.setStop(true)
					.setRegisteredDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
					.setSiteId(reqLocalJobAddDTO.getSiteId())
					.setSiteVo(siteRepository.findBySiteId(reqLocalJobAddDTO.getSiteId()));

			int jobId = jobRepository.save(jobEntity).getJobId();

			ResJobIdDTO resJobIdDTO = new ResJobIdDTO()
					.setJobId(jobId);

			for(ReqJobStepAddDTO stepAddDTO : reqLocalJobAddDTO.getSteps()) {
				StepEntity stepEntity = AddStepDtoToVoMapper.INSTANCE.toEntity(stepAddDTO);
				stepEntity.setJobId(jobId)
						.setFileIndices(stepAddDTO.getFileIndices())
						.setJobEntity(jobEntity);

				stepRepository.save(stepEntity);

				if(stepEntity.getStepType().equals(RunStep.STEPTYPE_CONVERT)) {
					ManualJobThread manualJobThread = new ManualJobThread(job, stepEntity, scheduler, scheduledTasks, true);
					Thread manualThread = new Thread(manualJobThread, "localThread-");
					manualThread.start();
				}
			}

			return resJobIdDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<ResPlanDTO> getPlanList(Integer siteId) throws Exception {
		try {
			SiteVo getSite = siteRepository.findBySiteId(siteId);

			if(getSite == null)
				new ResponseStatusException(HttpStatus.NOT_FOUND);

			String GET_PLAN_LIST_URL = String.format(ReqURLController.API_GET_PLAN_LIST_FROM_CRAS,
					getSite.getCrasAddress(),
					getSite.getCrasPort(),
					getSite.getRssAddress(),
					getSite.getRssPort(),
					getSite.getRssUserName(),
					getSite.getRssPassword());

			CallRestAPI callRestAPI = new CallRestAPI();
			ResponseEntity<?> response = callRestAPI.getRestAPI(GET_PLAN_LIST_URL, ResRCPlanDTO[].class);
			List<ResRCPlanDTO> resRCPlanDTOList = Arrays.asList((ResRCPlanDTO[]) response.getBody());
			List<ResPlanDTO> newPlanList = new ArrayList<>();

			for (ResRCPlanDTO plan : resRCPlanDTOList) {
				ResPlanDTO data = new ResPlanDTO()
						.setPlanId(plan.getPlanId())
						.setPlanName(plan.getPlanName())
						.setPlanType(plan.getPlanType())
						.setMachineNames(plan.getMachineNames())
						.setStatus(plan.getStatus())
						.setDescription(plan.getDescription())
						.setMeasure(plan.getMeasure())
						.setError(plan.getError())
						.setDetail(plan.getDetailedStatus());

				if (plan.getPlanType().equals(PLAN_TYPE_FTP)) {
					data.setTargetNames(plan.getCategoryNames());
				} else if (plan.getPlanType().equals(PLAN_TYPE_VFTP_COMPAT)) {
					List<String> newTarget = new ArrayList<>();
					for (String command : plan.getCommands()) {
						if (command.equals("none")) {
							newTarget.add(String.format("get %s_%s.log", plan.getFrom(), plan.getTo()));
						} else {
							newTarget.add(String.format("get " + command + ".log", plan.getFrom(), plan.getTo()));
						}
					}
				} else if (plan.getPlanType().equals(PLAN_TYPE_VFTP_SSS)) {
					List<String> newTarget = new ArrayList<>();
					for (String command : plan.getCommands()) {
						newTarget.add(String.format("cd " + command, plan.getFrom(), plan.getTo()));
					}
				}
				newPlanList.add(data);
			}
			return newPlanList;
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void runManualExcute(int jobId, int stepId, Boolean manual) {
		try {
			StepEntity stepEntity = stepRepository.findByStepId(stepId);

			SchedulerStrategy manualSchedulerStrategy;

			switch (stepEntity.getStepType()) {
				case (RunStep.STEPTYPE_COLLECT):
				case (RunStep.STEPTYPE_CONVERT):
				case (RunStep.STEPTYPE_PURGE):
					manualSchedulerStrategy = job;
					break;
				case (RunStep.STEPTYPE_SUMMARY):
				case (RunStep.STEPTYPE_CRAS):
				case (RunStep.STEPTYPE_VERSION):
					manualSchedulerStrategy = report;
					break;
				case (RunStep.STEPTYPE_CUSTOM):
					manualSchedulerStrategy = custom;
					break;
				default:
					throw new IllegalStateException("Unexpected value: " + stepEntity.getStepType());
			}

			ManualJobThread manualJobThread = new ManualJobThread(
					manualSchedulerStrategy, stepEntity, scheduler, scheduledTasks, manual);
			Thread manualThread = new Thread(manualJobThread, "manualThread-");
			manualThread.start();
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}