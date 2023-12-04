package com.module.news.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.module.news.dto.ProjectExecuteStatusDto;
import com.module.news.dto.QueryReportDto;
import com.module.news.dto.ScheduleDto;
import com.module.news.service.DataBaseService;
import com.module.news.service.ScheduleService;
import com.module.news.utils.TypeArticlesEnum;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
	final Map<Integer, ProjectExecuteStatusDto> projectStatusMap = new HashMap<>();

	@Autowired
	DataBaseServiceImpl dataBaseServiceImpl;

	@Autowired
	DataBaseService dataBaseService;

	@Autowired
	NewsApiImpl news;

	@Override
	@Scheduled(fixedRate = 3000)
	public void getSchedules() {
		List<ScheduleDto> schedules = dataBaseServiceImpl.getSchedules();
		List<ScheduleDto> filteredSchedules = schedules.stream().filter(schedule -> schedule.getFk_source() == 2)
				.collect(Collectors.toList());

		for (ScheduleDto schedule : filteredSchedules) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
			LocalDateTime nextExecutionTime = LocalDateTime.parse(schedule.getNext_execution_time(), formatter);
			List<QueryReportDto> query = dataBaseServiceImpl.getQueryReport();
			List<QueryReportDto> queryProyect = query.stream()
					.filter(queryBD -> queryBD.getFk_project() == schedule.getFk_project()&&queryBD.getFk_source()==2)
					.collect(Collectors.toList());
			if (queryProyect.size() > 0) {
				Gson g = new Gson();
				String jsonString = g.toJson(queryProyect.get(0).getQueryreport());		
				
				if (!projectStatusMap.containsKey(schedule.getFk_project())) {
					executeFirst(schedule, nextExecutionTime, jsonString,queryProyect.get(0).getResourceId());
				} else {
					programarEjecucion(schedule, nextExecutionTime, jsonString,
							projectStatusMap.get(schedule.getFk_project()),queryProyect.get(0).getResourceId());
				}
			}
		}
	}

	private void executeFirst(ScheduleDto schedule, LocalDateTime nextExecutionTime, String jsonString,String resourceId) {
		JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
		LocalDate currentDate = LocalDate.now();
		LocalDate dateStart = currentDate.minusDays(7);
		DateTimeFormatter formatterJson = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String currentDateStr = currentDate.format(formatterJson);
		String dateStartStr = dateStart.format(formatterJson);

		JsonObject additionalData = new JsonObject();
		additionalData.addProperty("dateStart", dateStartStr);
		additionalData.addProperty("dateEnd", currentDateStr);

		JsonArray andArray = jsonObject.getAsJsonObject("query").getAsJsonObject("$query").getAsJsonArray("$and");
		andArray.add(additionalData);

		String finalJsonString = jsonObject.toString();
		String response = news.searchNewsApi(finalJsonString);

		int pages = extractedPage(response);

		saveBD(schedule, response,TypeArticlesEnum.NUEVO,resourceId);

		if (pages > 1) {
			int page = 2;
			while (page <= pages) {
				JsonObject jsonPage = jsonObject;
				jsonPage.addProperty("articlesPage", page);
				response = news.searchNewsApi(jsonPage.toString());
				saveBD(schedule, response,TypeArticlesEnum.NUEVO,resourceId);
				page = page + 1;
			}
		}

		String lastNewsUri = null;
		String lastBlogUri = null;

		JsonObject finalJson = JsonParser.parseString(response).getAsJsonObject();
		JsonArray results = finalJson.getAsJsonObject("articles").getAsJsonArray("results");

		for (JsonElement element : results) {
			JsonObject item = element.getAsJsonObject();
			String dataType = item.get("dataType").getAsString();
			String uri = item.get("uri").getAsString();

			if ("news".equals(dataType)) {
				lastNewsUri = uri;
			} else if ("blog".equals(dataType)) {
				lastBlogUri = uri;
			}
		}

		projectStatusMap.put(schedule.getFk_project(),
				new ProjectExecuteStatusDto(nextExecutionTime, 0, lastNewsUri, lastBlogUri));
	}

	private void saveBD(ScheduleDto schedule, String response, TypeArticlesEnum type, String resourceId) {
		String jsonStringModificado="";
		if (type.equals(TypeArticlesEnum.NUEVO)) {
			jsonStringModificado = extractedBody(response);
		}
		if (type.equals(TypeArticlesEnum.ACTUALIZACION)) {
			jsonStringModificado = extractedBodyUpdate(response);
		}
		jsonStringModificado.replace("\n", "").replace("'", "").replace("-", "");
		Boolean execute = dataBaseService.saveNews(jsonStringModificado, resourceId);
		if (execute) {
			log.info("Se guardo correctamente");
		} else {
			log.error("No se guardo");
		}
	}

	private String extractedBodyUpdate(String response) {
		JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
		JsonArray resultsArray = jsonObject.getAsJsonObject("recentActivityArticles").get("activity").getAsJsonArray();
		for (int i = 0; i < resultsArray.size(); i++) {
			String body = resultsArray.get(i).getAsJsonObject().get("body").getAsString();
			String title = resultsArray.get(i).getAsJsonObject().get("title").getAsString();
			String description= resultsArray.get(i).getAsJsonObject().get("source").getAsJsonObject().get("description").getAsString();
			String descriptionSin=description.replace("\n", "");
			descriptionSin=description.replace("\r", "");
			descriptionSin=description.replace("\"", "");
			descriptionSin=description.replace("'", "");
			resultsArray.get(i).getAsJsonObject().get("source").getAsJsonObject().addProperty("description", descriptionSin);
			String titleSource= resultsArray.get(i).getAsJsonObject().get("source").getAsJsonObject().get("title").getAsString();
			String titleSourcesin=titleSource.replace("\n", "");
			titleSourcesin=titleSource.replace("\r", "");
			titleSourcesin=titleSource.replace("\"", "");
			titleSourcesin=titleSource.replace("'", "");
			resultsArray.get(i).getAsJsonObject().get("source").getAsJsonObject().addProperty("title", titleSourcesin);
			String titleSinComillas = title.replace("\n", "");
			titleSinComillas = title.replace("\r", "");
			titleSinComillas = title.replace("\"", "");
			titleSinComillas = titleSinComillas.replace("'", "");
			titleSinComillas = titleSinComillas.replace("\\", "");
			resultsArray.get(i).getAsJsonObject().addProperty("title", titleSinComillas);
			String bodySinComillas = body.replace("\n", "");
			bodySinComillas = bodySinComillas.replace("\r", "");
			bodySinComillas = bodySinComillas.replace("\"", "");
			bodySinComillas = bodySinComillas.replace("'", "");
			bodySinComillas = bodySinComillas.replace("\\", "");
			resultsArray.get(i).getAsJsonObject().addProperty("body", bodySinComillas);
		}
		String jsonStringModificado = jsonObject.toString();
		return jsonStringModificado;
	}

	private int extractedPage(String response) {
		JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
		int pages = jsonResponse.getAsJsonObject("articles").get("pages").getAsInt();
		return pages;
	}

	private String extractedBody(String response) {
		JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
		JsonArray resultsArray = jsonObject.getAsJsonObject("articles").get("results").getAsJsonArray();
		for (int i = 0; i < resultsArray.size(); i++) {
			String body = resultsArray.get(i).getAsJsonObject().get("body").getAsString();
			String title = resultsArray.get(i).getAsJsonObject().get("title").getAsString();
			String titleSinComillas = title.replace("\"", "");
			titleSinComillas = titleSinComillas.replace("\n", "");
			titleSinComillas = titleSinComillas.replace("'", "");
			titleSinComillas = titleSinComillas.replace("\\", "");
			resultsArray.get(i).getAsJsonObject().addProperty("title", titleSinComillas);
			String bodySinComillas = body.replace("\"", "");
			bodySinComillas = bodySinComillas.replace("\n", "");
			bodySinComillas = bodySinComillas.replace("'", "");
			bodySinComillas = bodySinComillas.replace("\\", "");
			resultsArray.get(i).getAsJsonObject().addProperty("body", bodySinComillas);
			String description= resultsArray.get(i).getAsJsonObject().get("source").getAsJsonObject().get("description").getAsString();
			String descriptionSin=description.replace("\n", "");
			descriptionSin=descriptionSin.replace("\r", "");
			descriptionSin=descriptionSin.replace("\"", "");
			descriptionSin=descriptionSin.replace("'", "");
			resultsArray.get(i).getAsJsonObject().get("source").getAsJsonObject().addProperty("description", descriptionSin);
			String titleSource= resultsArray.get(i).getAsJsonObject().get("source").getAsJsonObject().get("title").getAsString();
			String titleSourcesin=titleSource.replace("\n", "");
			titleSourcesin=titleSourcesin.replace("\r", "");
			titleSourcesin=titleSourcesin.replace("\"", "");
			titleSourcesin=titleSourcesin.replace("'", "");
			resultsArray.get(i).getAsJsonObject().get("source").getAsJsonObject().addProperty("title", titleSourcesin);
		}
		String jsonStringModificado = jsonObject.toString();
		log.info(jsonStringModificado);
		return jsonStringModificado;
	}

	private void programarEjecucion(ScheduleDto schedule, LocalDateTime nextExecutionTime, String jsonString,
			ProjectExecuteStatusDto projectExecuteStatusDto,String resourceId) {
		if (projectExecuteStatusDto.getStatus() == 0) {
			JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
			jsonObject.addProperty("recentActivityArticlesNewsUpdatesAfterUri",
					projectExecuteStatusDto.getLastNewsUri());
			jsonObject.addProperty("recentActivityArticlesBlogUpdatesAfterUri",
					projectExecuteStatusDto.getLastBlogUri());

			ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
			long delayInSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), nextExecutionTime);
			projectExecuteStatusDto.setStatus(1);
			executorService.schedule(() -> {
				String response = news.searchNewsApiUpdate(jsonObject.toString());
				saveBD(schedule, response,TypeArticlesEnum.ACTUALIZACION,resourceId);
				projectExecuteStatusDto.setStatus(2);
				dataBaseService.updateExecution(schedule.getIDExecution());
			}, delayInSeconds, TimeUnit.SECONDS);
		}
	}

}
