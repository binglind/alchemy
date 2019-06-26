package com.dfire.platform.alchemy.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.dfire.platform.alchemy.domain.enumeration.ClusterType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.dfire.platform.alchemy.domain.Cluster} entity. This class is used
 * in {@link com.dfire.platform.alchemy.web.rest.ClusterResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /clusters?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ClusterCriteria implements Serializable, Criteria {
    /**
     * Class for filtering ClusterType
     */
    public static class ClusterTypeFilter extends Filter<ClusterType> {

        public ClusterTypeFilter() {
        }

        public ClusterTypeFilter(ClusterTypeFilter filter) {
            super(filter);
        }

        @Override
        public ClusterTypeFilter copy() {
            return new ClusterTypeFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private ClusterTypeFilter type;

    private StringFilter remark;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter businessId;

    private LongFilter jobId;

    public ClusterCriteria(){
    }

    public ClusterCriteria(ClusterCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.remark = other.remark == null ? null : other.remark.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.businessId = other.businessId == null ? null : other.businessId.copy();
        this.jobId = other.jobId == null ? null : other.jobId.copy();
    }

    @Override
    public ClusterCriteria copy() {
        return new ClusterCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public ClusterTypeFilter getType() {
        return type;
    }

    public void setType(ClusterTypeFilter type) {
        this.type = type;
    }

    public StringFilter getRemark() {
        return remark;
    }

    public void setRemark(StringFilter remark) {
        this.remark = remark;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LongFilter getBusinessId() {
        return businessId;
    }

    public void setBusinessId(LongFilter businessId) {
        this.businessId = businessId;
    }

    public LongFilter getJobId() {
        return jobId;
    }

    public void setJobId(LongFilter jobId) {
        this.jobId = jobId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ClusterCriteria that = (ClusterCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            Objects.equals(remark, that.remark) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(businessId, that.businessId) &&
            Objects.equals(jobId, that.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        type,
        remark,
        createdBy,
        createdDate,
        lastModifiedBy,
        lastModifiedDate,
        businessId,
        jobId
        );
    }

    @Override
    public String toString() {
        return "ClusterCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (remark != null ? "remark=" + remark + ", " : "") +
                (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
                (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
                (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
                (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
                (businessId != null ? "businessId=" + businessId + ", " : "") +
                (jobId != null ? "jobId=" + jobId + ", " : "") +
            "}";
    }

}
