package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Boolean role = true - user question
 * Boolean role = false - answer from gigachat
 */
@Getter
@Setter
@Component
public class History {
    private Boolean role;
    private Queue<String> data = new LinkedList<>();
    private static final int MAX_SIZE = 3;

    public void addData(String item) {
        if (data.size() >= MAX_SIZE) {
            data.poll();
        }
        data.offer(item);
    }


}