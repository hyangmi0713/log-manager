package jp.co.canon.rss.logmanager.vo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
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
@Table(name = "log_download_status", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class LogDownloadStatusVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "site_id", nullable = false)
    private int siteId;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "rid_cras_search")
    private String ridCrasSearch;
    @Column(name = "rid_cras_download")
    private String ridCrasDownload;

    @Type(type = "string-array")
    @Column(name = "download_url", columnDefinition = "text []")
    private String [] downloadURL;
    @Column(name = "status")
    private String status;
    @Type(type = "string-array")
    @Column(name = "error", columnDefinition = "text []")
    private String [] error;

    @ManyToOne
    @JoinColumn(name = "site_vo_list_fk")
    private SiteVo siteVoList;
}
