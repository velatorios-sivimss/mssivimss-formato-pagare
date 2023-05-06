package com.imss.sivimss.formatopagare.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagareServicioDto {
	
	private Integer id;
	private Integer idODS;
	private Integer importe;
	private Integer redito;

}
