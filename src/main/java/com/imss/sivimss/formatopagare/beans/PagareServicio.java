package com.imss.sivimss.formatopagare.beans;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.formatopagare.util.AppConstantes;
import com.imss.sivimss.formatopagare.model.request.BusquedaDto;
import com.imss.sivimss.formatopagare.util.DatosRequest;
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
		String idPagare = request.getDatos().get("id").toString();
		StringBuilder query = new StringBuilder("");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		request.getDatos().remove("id");
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
}
