package com.module.news.dto;

import lombok.Data;

@Data
public class AuthorDto {
	private String uri;
	private String name;
	private String type;
	private boolean isAgency;
}
