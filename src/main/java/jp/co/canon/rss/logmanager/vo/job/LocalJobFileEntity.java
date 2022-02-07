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
@Table(name = "localjobfile", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class LocalJobFileEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer fileId;

	@Column(name = "file_name", nullable = false)
	private String fileName;
	@Column(name = "original_file_name", nullable = false)
	private String originalFileName;

	@ManyToOne
	@JoinColumn(name = "step_fk")
	private StepEntity stepEntity;
}


