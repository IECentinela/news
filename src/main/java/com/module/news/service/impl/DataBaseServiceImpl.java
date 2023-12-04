package com.module.news.service.impl;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.module.news.dto.QueryReportDto;
import com.module.news.dto.RQQueryReport;
import com.module.news.dto.ScheduleDto;
import com.module.news.service.DataBaseService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataBaseServiceImpl implements DataBaseService {
	private final EntityManager entityManager;

	public DataBaseServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Boolean saveQueryReport(RQQueryReport queryReport) {
		StoredProcedureQuery storedProcedureQuery = entityManager
				.createStoredProcedureQuery("centineladb.sp_saveQueryReportBinder");
		storedProcedureQuery.registerStoredProcedureParameter("in_queryreport", String.class, ParameterMode.IN);
		storedProcedureQuery.registerStoredProcedureParameter("in_resourceid", String.class, ParameterMode.IN);
		storedProcedureQuery.registerStoredProcedureParameter("in_typereport", Integer.class, ParameterMode.IN);
		storedProcedureQuery.registerStoredProcedureParameter("in_fkproject", Integer.class, ParameterMode.IN);
		storedProcedureQuery.registerStoredProcedureParameter("in_startDate", Date.class, ParameterMode.IN);
		storedProcedureQuery.registerStoredProcedureParameter("in_endDate", Date.class, ParameterMode.IN);
		storedProcedureQuery.registerStoredProcedureParameter("response", String.class, ParameterMode.OUT);
		String idResource = UUID.randomUUID().toString();
		storedProcedureQuery.setParameter("in_queryreport", queryReport.getJson());
		storedProcedureQuery.setParameter("in_resourceid", idResource);
		storedProcedureQuery.setParameter("in_typereport", 3);
		storedProcedureQuery.setParameter("in_fkproject", queryReport.getIdProyecto());
		storedProcedureQuery.setParameter("in_startDate", queryReport.getFechaInicio());
		storedProcedureQuery.setParameter("in_endDate", queryReport.getFechaFin());

		storedProcedureQuery.execute();

		String respuesta = (String) storedProcedureQuery.getOutputParameterValue("response");
		if (respuesta.contains("200")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Boolean saveNews(String json, String resourceId) {
		StoredProcedureQuery storedProcedureQuery = entityManager
				.createStoredProcedureQuery("centineladb.sp_processDataNews");
		storedProcedureQuery.registerStoredProcedureParameter("in_jsonData", String.class, ParameterMode.IN);
		storedProcedureQuery.registerStoredProcedureParameter("in_resourceId", String.class, ParameterMode.IN);
		storedProcedureQuery.registerStoredProcedureParameter("response", String.class, ParameterMode.OUT);
		log.info(json);
		storedProcedureQuery.setParameter("in_jsonData", json);
		storedProcedureQuery.setParameter("in_resourceId", resourceId);

		storedProcedureQuery.execute();

		String respuesta = (String) storedProcedureQuery.getOutputParameterValue("response");
		if (respuesta.contains("200")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<ScheduleDto> getSchedules() {
		StoredProcedureQuery storedProcedureQuery = entityManager
				.createStoredProcedureQuery("centineladb.sp_getSchedules");
		storedProcedureQuery.registerStoredProcedureParameter("response", String.class, ParameterMode.OUT);
		storedProcedureQuery.execute();

		String respuesta = (String) storedProcedureQuery.getOutputParameterValue("response");
		if (respuesta != null) {
			Gson g = new Gson();
			Type listType = new TypeToken<List<ScheduleDto>>() {
			}.getType();
			return g.fromJson(respuesta, listType);
		} else {
			return null;
		}
	}

	@Override
	public List<QueryReportDto> getQueryReport() {
		StoredProcedureQuery storedProcedureQuery = entityManager
				.createStoredProcedureQuery("centineladb.sp_getActiveQueryReport");
		storedProcedureQuery.registerStoredProcedureParameter("response", String.class, ParameterMode.OUT);
		storedProcedureQuery.execute();

		String respuesta = (String) storedProcedureQuery.getOutputParameterValue("response");
		if (respuesta != null) {
			Gson g = new Gson();
			Type listType = new TypeToken<List<QueryReportDto>>() {
			}.getType();
			return g.fromJson(respuesta, listType);
		} else {
			return null;
		}
	}

	@Override
	public Boolean updateExecution(Integer idExecute) {
		StoredProcedureQuery storedProcedureQuery = entityManager
				.createStoredProcedureQuery("centineladb.sp_updateNextExecution");
		storedProcedureQuery.registerStoredProcedureParameter("in_IDExecution", Integer.class, ParameterMode.IN);
		storedProcedureQuery.registerStoredProcedureParameter("response", String.class, ParameterMode.OUT);
		storedProcedureQuery.setParameter("in_IDExecution", idExecute);

		storedProcedureQuery.execute();

		String respuesta = (String) storedProcedureQuery.getOutputParameterValue("response");
		if (respuesta != null) {
			return true;
		} else {
			return false;
		}
	}
}