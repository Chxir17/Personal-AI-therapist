package com.aitherapist.aitherapist.domain.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DynamicStatus {
    private Status baseStatus;  
    private Long associatedId;
    private String telephone;
    public DynamicStatus(Status baseStatus, Long associatedId) {
        this.baseStatus = baseStatus;
        this.associatedId = associatedId;
    }


    public boolean is(Status status) {
        return this.baseStatus == status;
    }

    @Override
    public String toString() {
        return baseStatus.toString() + telephone.toString();
    }
}