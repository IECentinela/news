package com.module.news.dto;

import lombok.Data;

@Data
public class ConceptDto {
	private String uri;
    private String type;
    private int score;
    private LabelDto label;
    private String image;
    private SynonymsDto synonyms;
    private TrendingScoreDto trendingScore;
    private LocationDto location;
}
