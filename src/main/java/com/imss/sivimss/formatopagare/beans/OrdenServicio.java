package com.imss.sivimss.formatopagare.beans;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger log = LoggerFactory.getLogger(OrdenServicio.class);
	
	public DatosRequest obtenerODS(DatosRequest request, BusquedaDto busqueda, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = armaQuery(formatoFecha);
		if (busqueda.getIdDelegacion() != null) {
			query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
		} 
		if (busqueda.getIdVelatorio() != null) {
			query.append(" AND os.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
		} 
		if (busqueda.getIdDelegacion() == null && busqueda.getIdVelatorio() == null) {
			query.append(" AND os.ID_VELATORIO = 99");
		}
		query.append(" ORDER BY os.FEC_ALTA ASC");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		return request;
	}
	
	public DatosRequest listadoODS(BusquedaDto busqueda) throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		StringBuilder query = new StringBuilder("SELECT os.ID_ORDEN_SERVICIO, os.CVE_FOLIO \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_VELATORIO vel ON (os.ID_VELATORIO = vel.ID_VELATORIO) \n");
		query.append("JOIN SVT_PAGO_BITACORA pb ON (os.ID_ORDEN_SERVICIO = pb.ID_REGISTRO AND pb.ID_FLUJO_PAGOS = 1) \n");
		query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO = 2 \n");
		query.append("AND pb.CVE_ESTATUS_PAGO IN (2, 8) ");
		if (busqueda.getIdDelegacion() != null) {
			query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
		} 
		if (busqueda.getIdVelatorio() != null) {
			query.append(" AND vel.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
		}
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest getContratante(DatosRequest request) throws UnsupportedEncodingException {
		String idODS = request.getDatos().get("id").toString();
		StringBuilder query = new StringBuilder("SELECT CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomContratante \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("WHERE os.ID_ORDEN_SERVICIO = " + idODS);

		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		return request;
	}
	
	public DatosRequest buscarODS(DatosRequest request, BusquedaDto busqueda, String formatoFecha) throws UnsupportedEncodingException {
			
	    	StringBuilder query = armaQuery(formatoFecha);
	    	
	    	if (busqueda.getIdVelatorio() != null) {
				query.append(" AND os.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
			}
	    	if (busqueda.getIdDelegacion() != null) {
	    		query.append(" AND vel.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
	    	}

	    	if (busqueda.getFolioODS() != null) {
	    	    query.append(" AND os.CVE_FOLIO = '" + busqueda.getFolioODS() +"' ");
	    	}
	    	if (busqueda.getNomContratante() != null) {
	    		query.append(" AND CONCAT(IFNULL(prc.NOM_PERSONA,' '),' ',IFNULL(prc.NOM_PRIMER_APELLIDO,' '),' ' ,IFNULL(prc.NOM_SEGUNDO_APELLIDO,' ')) LIKE '%" + busqueda.getNomContratante().toUpperCase() + "%'");
	    	}
	    	if (busqueda.getFecIniODS() != null) {
	    		query.append(" AND DATE(os.FEC_ALTA) BETWEEN STR_TO_DATE('" + busqueda.getFecIniODS() + "','" + formatoFecha + "') AND STR_TO_DATE('" + busqueda.getFecFinODS() + "','" + formatoFecha + "')");
	    	}
	    	query.append(" ORDER BY os.FEC_ALTA ASC");

			log.info(query.toString());
	    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
			request.getDatos().put(AppConstantes.QUERY, encoded);
	    	
			return request;
	}
	 
    private StringBuilder armaQuery(String formatoFecha) {
    	StringBuilder query = new StringBuilder("SELECT DISTINCT os.ID_ORDEN_SERVICIO AS id, os.CVE_FOLIO AS folioODS, DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') AS fechaODS, os.ID_CONTRATANTE AS idContratante, ");
    	query.append("CONCAT(IFNULL(prc.NOM_PERSONA,' '),' ',IFNULL(prc.NOM_PRIMER_APELLIDO,' '),' ' ,IFNULL(prc.NOM_SEGUNDO_APELLIDO,' ')) AS nomContratante, \n");
    	query.append("fin.ID_FINADO AS idFinado,  \n");
    	query.append("CONCAT(prf.NOM_PERSONA,' ',prf.NOM_PRIMER_APELLIDO,' ',prf.NOM_SEGUNDO_APELLIDO) AS nomFinado,  \n");
    	query.append("'Generada' AS estatusODS, CASE WHEN pb.CVE_ESTATUS_PAGO = 8 THEN 'Pendiente' ELSE 'Generado' END AS estatusPago \n");
    	query.append("FROM SVC_ORDEN_SERVICIO os \n");
    	query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
    	query.append("LEFT JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
    	query.append("JOIN SVC_FINADO fin ON (os.ID_ORDEN_SERVICIO = fin.ID_ORDEN_SERVICIO) \n");
    	query.append("LEFT JOIN SVC_PERSONA prf ON (fin.ID_PERSONA = prf.ID_PERSONA) \n");
    	query.append("JOIN SVT_PAGO_BITACORA pb ON (os.ID_ORDEN_SERVICIO = pb.ID_REGISTRO AND pb.ID_FLUJO_PAGOS = 1) \n");
    	query.append("JOIN SVC_VELATORIO vel ON (vel.ID_VELATORIO = os.ID_VELATORIO) \n");
    	query.append("WHERE os.ID_ESTATUS_ORDEN_SERVICIO = 2 \n");
    	query.append("AND pb.CVE_ESTATUS_PAGO IN (2, 8) \n");
		
		return query;
    }
    
    public Map<String, Object> generarReporte(BusquedaDto reporteDto,String nombrePdfReportes, String formatoFecha){
		Map<String, Object> envioDatos = new HashMap<>();
		StringBuilder condicion = new StringBuilder(" ");
		if (reporteDto.getIdVelatorio() != null) {
			condicion.append(" AND os.ID_VELATORIO = ").append(reporteDto.getIdVelatorio());
		}
		if (reporteDto.getFolioODS() != null) {
    	    condicion.append(" AND os.CVE_FOLIO = '" + reporteDto.getFolioODS() +"' ");
    	}
		if (reporteDto.getNomContratante() != null) {
			condicion.append(" AND CONCAT(IFNULL(prc.NOM_PERSONA,' '),' ',IFNULL(prc.NOM_PRIMER_APELLIDO,' '),' ' ,IFNULL(prc.NOM_SEGUNDO_APELLIDO,' ')) LIKE '%" + reporteDto.getNomContratante().toUpperCase() + "%'");
		}
    	if (reporteDto.getFecIniODS() != null) {
    		condicion.append(" AND DATE(os.FEC_ALTA) BETWEEN STR_TO_DATE('" + reporteDto.getFecIniODS() + "','" + formatoFecha + "') AND STR_TO_DATE('" + reporteDto.getFecFinODS() + "','" + formatoFecha + "')");
    	}
		
		envioDatos.put("condicion", condicion.toString());
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		if (reporteDto.getTipoReporte().equals("xls")) {
			envioDatos.put("IS_IGNORE_PAGINATION", true);
		}
		
		return envioDatos;
	}
	
	public DatosRequest buscarContratante(DatosRequest request, BusquedaDto busqueda) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT CONCAT(sp.NOM_PERSONA,' ',sp.NOM_PRIMER_APELLIDO,' ',sp.NOM_SEGUNDO_APELLIDO) AS nomContratante ");
			query.append(" FROM SVC_CONTRATANTE sc ");
			query.append(" JOIN SVC_PERSONA sp ON sp.ID_PERSONA = sc.ID_PERSONA ");
			query.append(" WHERE UPPER(sp.NOM_PERSONA)  LIKE '%" + busqueda.getNomContratante().toUpperCase() + "%' ");
			query.append(" GROUP BY 1");
		log.info(query.toString());
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		return request;
	}
}
