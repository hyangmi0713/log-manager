package jp.co.canon.rss.logmanager.vo.job;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "history", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer historyId;
	@Column(name = "job_id", nullable = false)
	private int jobId;
	@Column(name = "step_id", nullable = false)
	private int stepId;

	@Column(name = "step_type", nullable = false)
	private String stepType;
	@Column(name = "run_date")
	private String runDate;
	@Column(name = "end_date")
	private String endDate;

	@Column(name = "manual")
	private Boolean isManual;

	@Column(name = "status", nullable = false)
	private String status;
	@Type(type = "string-array")
	@Column(name = "error", columnDefinition = "text []")
	private String [] error;

	@Column(name = "rid")
	private String rid;
	@Column(name = "rid_cras")
	private String ridCras;

	@ManyToOne
	@JoinColumn(name = "job_fk")
	private JobEntity jobEntity;

	@ManyToOne
	@JoinColumn(name = "step_fk")
	private StepEntity stepEntity;
}


