package com.module.news.controller;

import com.module.news.dto.RQQueryReport;
import com.module.news.dto.RQUpdateQR;
import com.module.news.service.DataBaseService;
import com.module.news.service.impl.NewsApiImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
	public ResponseEntity<String> saveQueryReport(@RequestBody RQQueryReport queryReport) {
		Boolean response = dataBaseService.saveQueryReport(queryReport);
		if(response) {
			return ResponseEntity.status(HttpStatus.CREATED).body("Reporte creado correctamente");
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("El reporte no se guardo");
		}
	}

	@ApiOperation(value = "Actualiza Query Report", response = String.class)
	@PostMapping("/updatereport")
	public ResponseEntity<String> updateQueryReport(@RequestBody RQUpdateQR queryReport) {
		Boolean response = dataBaseService.updateQueryReport(queryReport);
		if (response) {
			return ResponseEntity.status(HttpStatus.CREATED).body("Reporte actualizado correctamente");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("El reporte no se guardo");
		}
	}

	@GetMapping("/test")
	public void test() {
		String response=news.searchNewsApi("");
		dataBaseService.saveNews(response, "985b94b0-ba7b-4114-bbde-e9a36c84b7dd");
	}
}
