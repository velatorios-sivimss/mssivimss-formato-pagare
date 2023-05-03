package com.imss.sivimss.formatopagare.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
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
	
	private String fechaPagare;
	
	private String nomUsuario;

}
