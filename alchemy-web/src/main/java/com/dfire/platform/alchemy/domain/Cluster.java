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

import com.dfire.platform.alchemy.domain.enumeration.ClusterType;

/**
 * A Cluster.
 */
@Entity
@Table(name = "cluster")
public class Cluster implements Serializable {

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
    private ClusterType type;

    
    @Lob
    @Column(name = "config", nullable = false)
    private String config;

    @NotNull
    @Column(name = "remark", nullable = false)
    private String remark;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @ManyToOne
    @JsonIgnoreProperties("clusters")
    private Business business;

    @OneToMany(mappedBy = "cluster")
    private Set<Job> jobs = new HashSet<>();

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

    public Cluster name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClusterType getType() {
        return type;
    }

    public Cluster type(ClusterType type) {
        this.type = type;
        return this;
    }

    public void setType(ClusterType type) {
        this.type = type;
    }

    public String getConfig() {
        return config;
    }

    public Cluster config(String config) {
        this.config = config;
        return this;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getRemark() {
        return remark;
    }

    public Cluster remark(String remark) {
        this.remark = remark;
        return this;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Cluster createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Cluster createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Cluster lastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Cluster lastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Business getBusiness() {
        return business;
    }

    public Cluster business(Business business) {
        this.business = business;
        return this;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public Cluster jobs(Set<Job> jobs) {
        this.jobs = jobs;
        return this;
    }

    public Cluster addJob(Job job) {
        this.jobs.add(job);
        job.setCluster(this);
        return this;
    }

    public Cluster removeJob(Job job) {
        this.jobs.remove(job);
        job.setCluster(null);
        return this;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cluster)) {
            return false;
        }
        return id != null && id.equals(((Cluster) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Cluster{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", config='" + getConfig() + "'" +
            ", remark='" + getRemark() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
