package com.j10d207.tripeer.plan.db.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class PlanDetailVO {

    @NotNull (message = "${${validatedValue} is null")
    private long planDayId;
    @NotNull (message = "${${validatedValue} is null")
    private int spotInfoId;
    @NotNull (message = "${${validatedValue} is null")
    private int day;
    @NotNull (message = "${${validatedValue} is null")
    private int step;
    private LocalTime spotTime;
    private String description;
    private int cost;
}