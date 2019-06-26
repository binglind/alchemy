package com.dfire.platform.alchemy.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.dfire.platform.alchemy.domain.enumeration.TableType;

import com.dfire.platform.alchemy.domain.enumeration.SourceType;

/**
 * A Source.
 */
@Entity
@Table(name = "source")
public class Source implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "table_type", nullable = false)
    private TableType tableType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    
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
    @JsonIgnoreProperties("sources")
    private Business business;

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

    public Source name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TableType getTableType() {
        return tableType;
    }

    public Source tableType(TableType tableType) {
        this.tableType = tableType;
        return this;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public Source sourceType(SourceType sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getConfig() {
        return config;
    }

    public Source config(String config) {
        this.config = config;
        return this;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getRemark() {
        return remark;
    }

    public Source remark(String remark) {
        this.remark = remark;
        return this;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Source createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Source createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Source lastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Source lastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Business getBusiness() {
        return business;
    }

    public Source business(Business business) {
        this.business = business;
        return this;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Source)) {
            return false;
        }
        return id != null && id.equals(((Source) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Source{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", tableType='" + getTableType() + "'" +
            ", sourceType='" + getSourceType() + "'" +
            ", config='" + getConfig() + "'" +
            ", remark='" + getRemark() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
