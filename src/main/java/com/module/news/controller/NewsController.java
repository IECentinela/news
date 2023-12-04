package com.module.news.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.module.news.dto.RQQueryReport;
import com.module.news.service.DataBaseService;
import com.module.news.service.impl.NewsApiImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@CrossOrigin
@RestController
@RequestMapping("/news")
@Api(value = "News Controller", tags = "News Management")
public class NewsController {
	@Autowired
	DataBaseService dataBaseService;
	
	@Autowired
	NewsApiImpl news;
	
	@ApiOperation(value = "Crear un nuevo Query Report", response = String.class)
    @PostMapping("/savereport")
	public ResponseEntity<String> createUser(@RequestBody RQQueryReport queryReport) {
		Boolean response = dataBaseService.saveQueryReport(queryReport);
		if(response) {
			return ResponseEntity.status(HttpStatus.CREATED).body("Reporte creado correctamente");
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("El reporte no se guardo"); 
		}
	}
	
	@GetMapping("/test")
	public void test() {
		String response=news.searchNewsApi("{\"query\":{\"$query\":{\"$or\":[{\"keyword\":\"Harfuch\",\"keywordLoc\":\"body\"},{\"keyword\":\"Harfuch\",\"keywordLoc\":\"title\"},{\"dateStart\":\"2023-10-01\",\"dateEnd\":\"2023-10-09\"}]},\"$filter\":{\"dataType\":[\"news\",\"pr\",\"blog\"],\"isDuplicate\":\"skipDuplicates\"}},\"resultType\":\"articles\",\"articlesSortBy\":\"socialScore\",\"includeArticleSocialScore\":true,\"includeArticleConcepts\":true,\"includeArticleCategories\":true,\"includeArticleLocation\":true,\"includeArticleImage\":true,\"includeArticleVideos\":true,\"includeArticleLinks\":true,\"includeArticleExtractedDates\":true,\"includeArticleDuplicateList\":true,\"includeArticleOriginalArticle\":true,\"includeConceptImage\":true,\"includeConceptDescription\":true,\"includeConceptSynonyms\":true,\"includeConceptTrendingScore\":true,\"includeSourceDescription\":true,\"includeSourceLocation\":true,\"includeSourceRanking\":true,\"apiKey\":\"98f827b5-dc32-4dcb-a768-1c6c291c80cd\"}");
		dataBaseService.saveNews(response, "5182cbc0-cd88-477d-a450-14e87e87446b");
	}
}
