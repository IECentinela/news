package com.module.news.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.module.news.service.NewsApi;
import com.module.news.utils.NewsApiProperties;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class NewsApiImpl implements NewsApi {
	
	private final NewsApiProperties apiProperties;
	
	 @Autowired
	    public NewsApiImpl(NewsApiProperties apiProperties) {
	        this.apiProperties = apiProperties;
	    }
	@Override
	public String searchNewsApi(String body) {
		String apiUrl = apiProperties.getUrl();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

		// Verifica si la solicitud fue exitosa (código de estado 200 OK)
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			// Obtiene el cuerpo de la respuesta como una cadena
			String responseBody = responseEntity.getBody();
			log.info(responseBody);
			return responseBody;
		} else {
			// En caso de un error, puedes manejarlo según tus necesidades
			return "Error: La solicitud no fue exitosa. Código de estado: " + responseEntity.getStatusCodeValue();
		}
	}
	
	public String searchNewsApiUpdate(String body) {
		String apiUrl = apiProperties.getUrlUri();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

		// Verifica si la solicitud fue exitosa (código de estado 200 OK)
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			// Obtiene el cuerpo de la respuesta como una cadena
			String responseBody = responseEntity.getBody();
			log.info(responseBody);
			return responseBody;
		} else {
			// En caso de un error, puedes manejarlo según tus necesidades
			return "Error: La solicitud no fue exitosa. Código de estado: " + responseEntity.getStatusCodeValue();
		}
	}

}
