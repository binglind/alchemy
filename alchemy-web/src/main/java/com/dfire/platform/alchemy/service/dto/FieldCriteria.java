package com.dfire.platform.alchemy.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.dfire.platform.alchemy.domain.Field} entity. This class is used
 * in {@link com.dfire.platform.alchemy.web.rest.FieldResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /fields?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FieldCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter columnName;

    private StringFilter columnType;

    private StringFilter config;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter sourceId;

    public FieldCriteria(){
    }

    public FieldCriteria(FieldCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.columnName = other.columnName == null ? null : other.columnName.copy();
        this.columnType = other.columnType == null ? null : other.columnType.copy();
        this.config = other.config == null ? null : other.config.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.sourceId = other.sourceId == null ? null : other.sourceId.copy();
    }

    @Override
    public FieldCriteria copy() {
        return new FieldCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getColumnName() {
        return columnName;
    }

    public void setColumnName(StringFilter columnName) {
        this.columnName = columnName;
    }

    public StringFilter getColumnType() {
        return columnType;
    }

    public void setColumnType(StringFilter columnType) {
        this.columnType = columnType;
    }

    public StringFilter getConfig() {
        return config;
    }

    public void setConfig(StringFilter config) {
        this.config = config;
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

    public LongFilter getSourceId() {
        return sourceId;
    }

    public void setSourceId(LongFilter sourceId) {
        this.sourceId = sourceId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FieldCriteria that = (FieldCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(columnName, that.columnName) &&
            Objects.equals(columnType, that.columnType) &&
            Objects.equals(config, that.config) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(sourceId, that.sourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        columnName,
        columnType,
        config,
        createdBy,
        createdDate,
        lastModifiedBy,
        lastModifiedDate,
        sourceId
        );
    }

    @Override
    public String toString() {
        return "FieldCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (columnName != null ? "columnName=" + columnName + ", " : "") +
                (columnType != null ? "columnType=" + columnType + ", " : "") +
                (config != null ? "config=" + config + ", " : "") +
                (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
                (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
                (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
                (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
                (sourceId != null ? "sourceId=" + sourceId + ", " : "") +
            "}";
    }

}
