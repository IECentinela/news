package com.module.news.service;

import com.module.news.dto.QueryReportDto;
import com.module.news.dto.RQQueryReport;
import com.module.news.dto.RQUpdateQR;
import com.module.news.dto.ScheduleDto;

import java.util.List;

public interface DataBaseService {
    Boolean saveQueryReport(RQQueryReport queryReport);

    Boolean saveNews(String json, String resourceId);

    List<ScheduleDto> getSchedules();

    List<QueryReportDto> getQueryReport();

    Boolean updateExecution(Integer idExecute);

    Boolean updateQueryReport(RQUpdateQR queryReport);
}
