package com.module.news.dto;

import lombok.Data;

@Data
public class ScheduleDto {
	private int fk_source;
	private int fk_project;
	private int IDExecution;
	private Object task_description;
	private String next_execution_time;
}
