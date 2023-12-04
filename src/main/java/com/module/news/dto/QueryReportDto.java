package com.module.news.dto;

import lombok.Data;

@Data
public class QueryReportDto {
	private String endDate;
    private int fk_source;
    private String startDate;
    private int fk_project;
    private String resourceId;
    private Object queryreport;
}
