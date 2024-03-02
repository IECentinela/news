package com.module.news.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RQUpdateQR {
    private String json;
    private Integer idQueryReport;
    private Date fechaInicio;
    private Date fechaFin;
}
