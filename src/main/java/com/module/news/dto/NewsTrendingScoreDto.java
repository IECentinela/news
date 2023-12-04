package com.module.news.dto;

import lombok.Data;

@Data
public class NewsTrendingScoreDto {
	private double score;
    private int testPopFq;
    private int nullPopFq;
}
