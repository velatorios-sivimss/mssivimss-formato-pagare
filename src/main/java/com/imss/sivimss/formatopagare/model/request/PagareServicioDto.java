package com.imss.sivimss.formatopagare.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagareServicioDto {
	
	private Integer id;
	private Integer idODS;
	private Double importe;
	private Double redito;
	
}
