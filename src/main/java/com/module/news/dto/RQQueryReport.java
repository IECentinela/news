package com.module.news.dto;

import java.util.Date;

import lombok.Data;

@Data
public class RQQueryReport {
	private String json;
	private Integer idProyecto;
	private Date fechaInicio;
	private Date fechaFin;
}
