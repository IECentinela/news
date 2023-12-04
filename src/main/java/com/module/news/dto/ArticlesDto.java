package com.module.news.dto;

import java.util.List;

import lombok.Data;

@Data
public class ArticlesDto {
	private int page;
	private int totalResults;
	private int pages;
	private List<ResultDto> results;
}
