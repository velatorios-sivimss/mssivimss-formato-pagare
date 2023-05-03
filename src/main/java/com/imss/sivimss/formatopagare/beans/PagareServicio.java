package com.imss.sivimss.formatopagare.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.formatopagare.util.AppConstantes;
import com.imss.sivimss.formatopagare.model.request.BusquedaDto;
import com.imss.sivimss.formatopagare.util.DatosRequest;
import com.imss.sivimss.formatopagare.util.QueryHelper;
import com.imss.sivimss.formatopagare.util.ConvertirImporteLetra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class PagareServicio {

	private Integer id;
	private Integer importe;
	
	public String importeLetra() {
		return ConvertirImporteLetra.importeEnTexto(this.importe);
	}
	
	
	public DatosRequest detallPagare(DatosRequest request) {
		//String idPagare = request.getDatos().get("datos").get("id").toString();
		StringBuilder query = new StringBuilder("SELECT 2 AS detalle FROM DUAL");
		// Query
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		request.getDatos().remove("id");
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest generaPagare() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("");
		// Armar query
		String query = q.obtenerQueryInsertar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
	}
	
}
