package com.module.news.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProjectExecuteStatusDto {
	private LocalDateTime nextExecutionTime;
	private Integer status;
	private String lastNewsUri;
	private String lastBlogUri;

	public ProjectExecuteStatusDto(LocalDateTime nextExecutionTime, Integer status, String lastNewsUri,
			String lastBlogUri) {
		super();
		this.nextExecutionTime = nextExecutionTime;
		this.status = status;
		this.lastNewsUri = lastNewsUri;
		this.lastBlogUri = lastBlogUri;
	}

}
