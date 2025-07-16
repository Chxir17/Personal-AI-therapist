package com.aitherapist.aitherapist.domain.enums;

public class DynamicStatus {
    private Status baseStatus;  
    private Long associatedId;
    public DynamicStatus(Status baseStatus, Long associatedId) {
        this.baseStatus = baseStatus;
        this.associatedId = associatedId;
    }


    public Status getBaseStatus() {
        return baseStatus;
    }

    public Long getAssociatedId() {
        return associatedId;
    }

    public boolean is(Status status) {
        return this.baseStatus == status;
    }
}