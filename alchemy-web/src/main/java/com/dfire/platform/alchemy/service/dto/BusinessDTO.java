package com.dfire.platform.alchemy.service.dto;
import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dfire.platform.alchemy.domain.Business} entity.
 */
public class BusinessDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String remark;

    private String createdBy;

    private Instant createdDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BusinessDTO businessDTO = (BusinessDTO) o;
        if (businessDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), businessDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BusinessDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", remark='" + getRemark() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}
