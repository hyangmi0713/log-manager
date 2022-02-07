package jp.co.canon.rss.logmanager.vo.job;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@TypeDef(name = "int-array", typeClass = IntArrayType.class)
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "step", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class StepEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer stepId;
	@Column(name = "job_id", nullable = false)
	private int jobId;
	@Column(name = "uuid")
	private String uuid;

	@Column(name = "step_name")
	private String stepName;
	@Column(name = "enable", nullable = false)
	private Boolean enable;
	@Column(name = "step_type", nullable = false)
	private String stepType;
	@Column(name = "mode")
	private String mode;
	@Column(name = "period")
	private int period;
	@Type(type = "string-array")
	@Column(name = "time", columnDefinition = "text []")
	private String [] time;
	@Column(name = "cycle")
	private String cycle;
	@Type(type = "string-array")
	@Column(name = "cron", columnDefinition = "text []")
	private String [] cron;
	@Column(name = "pre_step")
	private String preStep;
	@Column(name = "is_email")
	private Boolean isEmail;
	@Type(type = "string-array")
	@Column(name = "custom_emails", columnDefinition = "text []")
	private String [] customEmails;
	@Type(type = "int-array")
	@Column(name = "email_book_ids", columnDefinition = "integer []")
	private int[] emailBookIds;
	@Type(type = "int-array")
	@Column(name = "group_book_ids", columnDefinition = "integer []")
	private int[] groupBookIds;
	@Column(name = "subject", columnDefinition = "TEXT")
	private String subject;
	@Column(name = "body", columnDefinition = "TEXT")
	private String body;
	@Column(name = "before")
	private int before;
	@Type(type = "int-array")
	@Column(name = "select_judge_rules", columnDefinition = "integer []")
	private int[] selectJudgeRuleIds;
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	@Column(name = "script_type")
	private String scriptType;
	@Column(name = "script", columnDefinition = "TEXT")
	private String script;

	@Type(type = "int-array")
	@Column(name = "local_file_id", columnDefinition = "integer []")
	private int[] fileIndices;

	@ManyToOne
	@JoinColumn(name = "job_fk")
	private JobEntity jobEntity;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "stepEntity")
	private List<LocalJobFileEntity> localJobFiles = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "stepEntity")
	private List<HistoryEntity> histories = new ArrayList<>();
}
