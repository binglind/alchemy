package com.dfire.platform.alchemy.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Business.
 */
@Entity
@Table(name = "business")
public class Business implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "remark", nullable = false)
    private String remark;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Instant createdDate;

    @OneToMany(mappedBy = "business")
    private Set<Job> jobs = new HashSet<>();

    @OneToMany(mappedBy = "business")
    private Set<Cluster> clusters = new HashSet<>();

    @OneToMany(mappedBy = "business")
    private Set<Source> sources = new HashSet<>();

    @OneToMany(mappedBy = "business")
    private Set<Sink> sinks = new HashSet<>();

    @OneToMany(mappedBy = "business")
    private Set<Udf> udfs = new HashSet<>();

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

    public Business name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public Business remark(String remark) {
        this.remark = remark;
        return this;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Business createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Business createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public Business jobs(Set<Job> jobs) {
        this.jobs = jobs;
        return this;
    }

    public Business addJob(Job job) {
        this.jobs.add(job);
        job.setBusiness(this);
        return this;
    }

    public Business removeJob(Job job) {
        this.jobs.remove(job);
        job.setBusiness(null);
        return this;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Set<Cluster> getClusters() {
        return clusters;
    }

    public Business clusters(Set<Cluster> clusters) {
        this.clusters = clusters;
        return this;
    }

    public Business addCluster(Cluster cluster) {
        this.clusters.add(cluster);
        cluster.setBusiness(this);
        return this;
    }

    public Business removeCluster(Cluster cluster) {
        this.clusters.remove(cluster);
        cluster.setBusiness(null);
        return this;
    }

    public void setClusters(Set<Cluster> clusters) {
        this.clusters = clusters;
    }

    public Set<Source> getSources() {
        return sources;
    }

    public Business sources(Set<Source> sources) {
        this.sources = sources;
        return this;
    }

    public Business addSource(Source source) {
        this.sources.add(source);
        source.setBusiness(this);
        return this;
    }

    public Business removeSource(Source source) {
        this.sources.remove(source);
        source.setBusiness(null);
        return this;
    }

    public void setSources(Set<Source> sources) {
        this.sources = sources;
    }

    public Set<Sink> getSinks() {
        return sinks;
    }

    public Business sinks(Set<Sink> sinks) {
        this.sinks = sinks;
        return this;
    }

    public Business addSink(Sink sink) {
        this.sinks.add(sink);
        sink.setBusiness(this);
        return this;
    }

    public Business removeSink(Sink sink) {
        this.sinks.remove(sink);
        sink.setBusiness(null);
        return this;
    }

    public void setSinks(Set<Sink> sinks) {
        this.sinks = sinks;
    }

    public Set<Udf> getUdfs() {
        return udfs;
    }

    public Business udfs(Set<Udf> udfs) {
        this.udfs = udfs;
        return this;
    }

    public Business addUdf(Udf udf) {
        this.udfs.add(udf);
        udf.setBusiness(this);
        return this;
    }

    public Business removeUdf(Udf udf) {
        this.udfs.remove(udf);
        udf.setBusiness(null);
        return this;
    }

    public void setUdfs(Set<Udf> udfs) {
        this.udfs = udfs;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Business)) {
            return false;
        }
        return id != null && id.equals(((Business) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Business{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", remark='" + getRemark() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}
