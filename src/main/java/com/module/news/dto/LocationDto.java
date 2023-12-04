package com.module.news.dto;

import lombok.Data;

@Data
public class LocationDto {
	private String type;
	private LabelDto label;
	private CountryDto country;
}
