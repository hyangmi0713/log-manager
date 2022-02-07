package jp.co.canon.rss.logmanager.vo.job;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@TypeDef(name = "int-array", typeClass = IntArrayType.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "job", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class JobEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer jobId;
	@Column(name = "site_id", nullable = false)
	private int siteId;

	@Column(name = "job_name")
	private String jobName;
	@Column(name = "type", nullable = false)
	private String type;
	@Column(name = "stop", nullable = false)
	private Boolean stop;
	@Column(name = "registered_date", nullable = false)
	private String registeredDate;
	@Type(type = "int-array")
	@Column(name = "plan_id", columnDefinition = "integer []")
	private int[] planIds;

	@ManyToOne
	@JoinColumn(name = "site_fk")
	private SiteVo siteVo;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "jobEntity")
	private List<StepEntity> steps = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "jobEntity")
	private List<HistoryEntity> histories = new ArrayList<>();
}
