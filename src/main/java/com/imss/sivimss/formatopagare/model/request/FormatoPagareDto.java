package com.imss.sivimss.formatopagare.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormatoPagareDto {
	
	private Integer idPagare;
	
	private String folioPagare;
	
	private Integer importe;
	
	private String redito;
	
	private String fechaODS;
	
	private String cantidad;
	
	private String nomContratante;
	
	private String domContratante;
	
	private String fechaPago;
	
	private String nomUsuario;

}
