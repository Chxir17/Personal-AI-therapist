package com.aitherapist.aitherapist.domain.enums;

import lombok.Getter;

@Getter
public class DynamicStatus {
    private Status baseStatus;  
    private Long associatedId;
    public DynamicStatus(Status baseStatus, Long associatedId) {
        this.baseStatus = baseStatus;
        this.associatedId = associatedId;
    }


    public boolean is(Status status) {
        return this.baseStatus == status;
    }
}