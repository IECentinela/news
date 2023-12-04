package com.module.news.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("news.api")
@Data
public class NewsApiProperties {
	private String url;
	private String urlUri;
}
