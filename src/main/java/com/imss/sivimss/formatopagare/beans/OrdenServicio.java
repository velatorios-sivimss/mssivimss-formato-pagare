package com.imss.sivimss.formatopagare.beans;

import java.util.HashMap;
import java.util.Map;

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
	
	public DatosRequest obtenerODS(DatosRequest request, BusquedaDto busqueda, String formatoFecha) {
		StringBuilder query = armaQuery(formatoFecha);
		if (busqueda.getIdOficina() > 1) {
			query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
			if (busqueda.getIdOficina() == 3) {
				query.append(" AND fin.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
			}
		} 
		query.append(" ORDER BY os.ID_ORDEN_SERVICIO DESC");
        
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		return request;
	}
	
	public DatosRequest buscarODS(DatosRequest request, BusquedaDto busqueda, String formatoFecha) {
			
	    	StringBuilder query = armaQuery(formatoFecha);
	    	if (busqueda.getIdNivel() > 1 && busqueda.getIdVelatorio() != null) {
				query.append(" AND fin.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
			}

	    	if (busqueda.getFolioODS() != null) {
	    	    query.append(" AND os.CVE_FOLIO = '" + busqueda.getFolioODS() +"' ");
	    	}
	    	if (busqueda.getNomContratante() != null) {
	    		query.append(" AND (prc.NOM_PERSONA LIKE '%" + busqueda.getNomContratante() + "%'");
	    		query.append(" OR prc.NOM_PRIMER_APELLIDO LIKE '%" + busqueda.getNomContratante() + "%'");
	    		query.append(" OR prc.NOM_SEGUNDO_APELLIDO LIKE '%" + busqueda.getNomContratante() + "%') \n");
	    	}
	    	if (busqueda.getFecIniODS() != null) {
	    	    query.append(" AND DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') BETWEEN '" + busqueda.getFecIniODS() + "' AND '" + busqueda.getFecFinODS() + "' \n");
	    	}
	    	query.append(" ORDER BY os.ID_ORDEN_SERVICIO DESC");
	    	
	    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes());
			request.getDatos().put(AppConstantes.QUERY, encoded);
	    	
			return request;
	}
	 
    private StringBuilder armaQuery(String formatoFecha) {
    	StringBuilder query = new StringBuilder("SELECT os.ID_ORDEN_SERVICIO AS id, os.CVE_FOLIO AS folioODS, DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') AS fechaODS, \n");
		query.append("os.ID_CONTRATANTE AS idContratante, \n");
		query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomContratante, \n");
		query.append("'Generada' AS estatusODS, CASE WHEN ISNULL(pb.CVE_ESTATUS_PAGO) THEN 'Pendiente' ELSE 'Generado' END AS estatusPago \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("LEFT JOIN SVC_INFORMACION_SERVICIO inf ON (os.ID_ORDEN_SERVICIO = inf.ID_ORDEN_SERVICIO) \n");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("LEFT JOIN SVT_PAGO_BITACORA pb ON (os.ID_ORDEN_SERVICIO = pb.ID_FLUJO_PAGOS) \n");
		query.append("LEFT JOIN SVC_VELATORIO vel ON (pb.ID_VELATORIO = vel.ID_VELATORIO) \n");
		query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO = 2 \n");
		
		return query;
    }
    
    public Map<String, Object> generarReporte(BusquedaDto reporteDto,String nombrePdfReportes, String formatoFecha){
		Map<String, Object> envioDatos = new HashMap<>();
		StringBuilder condicion = new StringBuilder(" ");
		if (reporteDto.getIdVelatorio() != null) {
			condicion.append(" AND fin.ID_VELATORIO = ").append(reporteDto.getIdVelatorio());
		}
		if (reporteDto.getFolioODS() != null) {
    	    condicion.append(" AND os.CVE_FOLIO = '" + reporteDto.getFolioODS() +"' ");
    	}
		if (reporteDto.getNomContratante() != null) {
			condicion.append(" AND (prc.NOM_PERSONA LIKE '%" + reporteDto.getNomContratante() + "%'");
    		condicion.append(" OR prc.NOM_PRIMER_APELLIDO LIKE '%" + reporteDto.getNomContratante() + "%'");
    		condicion.append(" OR prc.NOM_SEGUNDO_APELLIDO LIKE '%" + reporteDto.getNomContratante() + "%') \n");
		}
    	if (reporteDto.getFecIniODS() != null) {
    	    condicion.append(" AND DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') BETWEEN '" + reporteDto.getFecIniODS() + "' AND '" + reporteDto.getFecFinODS() + "' \n");
    	}
		
		envioDatos.put("condicion", condicion.toString());
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		
		return envioDatos;
	}
    
}
