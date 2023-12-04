package com.module.news.dto;

import lombok.Data;

@Data
public class SourceDto {
	private String uri;
	private String dataType;
	private String title;
	private String description;
	private Object location;
	private boolean locationValidated;
	private RankingDto ranking;
}
