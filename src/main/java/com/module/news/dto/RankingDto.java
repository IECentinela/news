package com.module.news.dto;

import lombok.Data;

@Data
public class RankingDto {
	private int importanceRank;
	private int alexaGlobalRank;
	private int alexaCountryRank;
}
