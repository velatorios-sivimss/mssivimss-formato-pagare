package com.imss.sivimss.formatopagare.beans;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.formatopagare.util.AppConstantes;
import com.imss.sivimss.formatopagare.model.request.BusquedaDto;
import com.imss.sivimss.formatopagare.util.DatosRequest;

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
public class OrdenServicio {
	
	private Integer id;
	private String fechaODS;
	private String folioODS;
	private Integer idContratante;
	private String nomContratante;
	private Integer estatusODS;
    private Integer estatusPago;
	
	private static final String fechaCotejo =  "DATE_FORMAT(inf.FEC_CORTEJO,'%d/%m/%Y')";
	
	public DatosRequest obtenerODS(DatosRequest request, BusquedaDto busqueda) {
		StringBuilder query = armaQuery();
		if (busqueda.getIdOficina() > 1) {
			query.append(" WHERE vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
			if (busqueda.getIdOficina() == 3) {
				query.append(" AND fin.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
			}
		} 
		query.append(" ORDER BY os.ID_ORDEN_SERVICIO DESC");
        
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		return request;
	}
	
	public DatosRequest buscarODS(DatosRequest request, BusquedaDto busqueda) {
			
	    	StringBuilder query = armaQuery();
	    	query.append("WHERE 1 = 1");
	    	if (busqueda.getIdNivel() > 1 && busqueda.getIdVelatorio() != null) {
				query.append(" AND fin.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
			}

	    	if (busqueda.getFolioODS() != null) {
	    	    query.append(" AND os.CVE_FOLIO = '" + busqueda.getFolioODS() +"' ");
	    	}
	    	if (busqueda.getNomContratante() != null) {
	    		query.append(" AND (prc.NOM_PERSONA LIKE %'" + busqueda.getNomContratante() + "%'");
	    		query.append(" OR prc.NOM_PRIMER_APELLIDO LIKE %'" + busqueda.getNomContratante() + "%'");
	    		query.append(" OR prc.NOM_SEGUNDO_APELLIDO LIKE %'" + busqueda.getNomContratante() + "%') \n");
	    	}
	    	if (busqueda.getFecIniODS() != null) {
	    	    query.append(" AND " + fechaCotejo + " BETWEEN '" + busqueda.getFecIniODS() + "' AND '" + busqueda.getFecFinODS() + "' \n");
	    	}
	    	query.append(" ORDER BY os.ID_ORDEN_SERVICIO DESC");
	    	
	    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
			request.getDatos().put(AppConstantes.QUERY, encoded);
	    	
			return request;
	}
	 
    private StringBuilder armaQuery() {
    	StringBuilder query = new StringBuilder("SELECT os.ID_ORDEN_SERVICIO AS id, os.CVE_FOLIO AS folioODS, " + fechaCotejo + " AS fechaODS, \n");
		query.append("os.ID_CONTRATANTE AS idContratante, \n");
		query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomContratante, \n");
		query.append("os.CVE_ESTATUS AS estatusODS, pb.CVE_ESTATUS_PAGO AS estatusPago \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("LEFT JOIN SVC_INFORMACION_SERVICIO inf ON (os.ID_ORDEN_SERVICIO = inf.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("LEFT JOIN SVT_PAGO_BITACORA pb ON (os.ID_ORDEN_SERVICIO = pb.ID_FLUJO_PAGOS) \n");
		query.append("LEFT JOIN SVC_VELATORIO vel ON (pb.ID_VELATORIO = vel.ID_VELATORIO) \n");
		
		return query;
    }
        
}
