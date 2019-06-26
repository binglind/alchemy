package com.dfire.platform.alchemy.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.dfire.platform.alchemy.domain.enumeration.JobType;

import com.dfire.platform.alchemy.domain.enumeration.JobStatus;

/**
 * A Job.
 */
@Entity
@Table(name = "job")
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_type", nullable = false)
    private JobType type;

    
    @Lob
    @Column(name = "config", nullable = false)
    private String config;

    @NotNull
    @Column(name = "remark", nullable = false)
    private String remark;

    @Column(name = "cluster_job_id")
    private String clusterJobId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JobStatus status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @ManyToOne
    @JsonIgnoreProperties("jobs")
    private Business business;

    @ManyToOne
    @JsonIgnoreProperties("jobs")
    private Cluster cluster;

    @OneToMany(mappedBy = "job")
    private Set<JobSql> sqls = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Job name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobType getType() {
        return type;
    }

    public Job type(JobType type) {
        this.type = type;
        return this;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public String getConfig() {
        return config;
    }

    public Job config(String config) {
        this.config = config;
        return this;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getRemark() {
        return remark;
    }

    public Job remark(String remark) {
        this.remark = remark;
        return this;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getClusterJobId() {
        return clusterJobId;
    }

    public Job clusterJobId(String clusterJobId) {
        this.clusterJobId = clusterJobId;
        return this;
    }

    public void setClusterJobId(String clusterJobId) {
        this.clusterJobId = clusterJobId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public Job status(JobStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Job createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Job createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Job lastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Job lastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Business getBusiness() {
        return business;
    }

    public Job business(Business business) {
        this.business = business;
        return this;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public Job cluster(Cluster cluster) {
        this.cluster = cluster;
        return this;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Set<JobSql> getSqls() {
        return sqls;
    }

    public Job sqls(Set<JobSql> jobSqls) {
        this.sqls = jobSqls;
        return this;
    }

    public Job addSql(JobSql jobSql) {
        this.sqls.add(jobSql);
        jobSql.setJob(this);
        return this;
    }

    public Job removeSql(JobSql jobSql) {
        this.sqls.remove(jobSql);
        jobSql.setJob(null);
        return this;
    }

    public void setSqls(Set<JobSql> jobSqls) {
        this.sqls = jobSqls;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Job)) {
            return false;
        }
        return id != null && id.equals(((Job) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Job{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", config='" + getConfig() + "'" +
            ", remark='" + getRemark() + "'" +
            ", clusterJobId='" + getClusterJobId() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
