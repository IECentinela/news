package com.module.news.dto;

import java.util.List;
import java.util.Locale.Category;

import lombok.Data;
@Data
public class ResultDto {
	private String uri;
    private String lang;
    private boolean isDuplicate;
    private String date;
    private String time;
    private String dateTime;
    private String dateTimePub;
    private String dataType;
    private double sim;
    private String url;
    private String title;
    private String body;
    private SourceDto source;
    private List<AuthorDto> authors;
    private List<ConceptDto> concepts;
    private List<Category> categories;
    private List<String> links;
    private List<VideoDto> videos;
    private String image;
    private List<String> duplicateList;
    private String originalArticle;
    private String eventUri;
    private LocationDto location;
    private Object extractedDates;
    private String shares;
    private Object sentiment;
    private int wgt;
    private int relevance;
}
