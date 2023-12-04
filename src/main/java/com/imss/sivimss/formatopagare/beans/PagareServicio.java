package com.imss.sivimss.formatopagare.beans;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.formatopagare.util.AppConstantes;
import com.imss.sivimss.formatopagare.model.request.FormatoPagareDto;
import com.imss.sivimss.formatopagare.model.request.PagareServicioDto;
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

	private static final Logger log = LoggerFactory.getLogger(PagareServicio.class);
	
	private Integer id;
	private Integer idODS;
	private Double importe;
	private Double redito;
	private Integer idUsuarioAlta;
	private Integer idUsuarioModifica;
	
	public PagareServicio(PagareServicioDto pagareDto) {
		this.idODS = pagareDto.getIdODS();
		this.importe = pagareDto.getImporte();
		this.redito = pagareDto.getRedito();
	}
	
	public String importeLetra() {
		return ConvertirImporteLetra.importeEnTexto(this.importe);
	}
	
	public DatosRequest detallPagare(DatosRequest request, String usuario, String formatoFecha) {
		StringBuilder query = new StringBuilder("SELECT os.CVE_FOLIO AS folioODS, DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') AS fechaODS,  ");
		query.append("TIME(os.FEC_ALTA) AS hora, pb.IMP_VALOR - IFNULL(SUM(pd.IMP_PAGO),0) AS importe, 6.0 AS redito, os.CVE_FOLIO AS folioPagare,  ");
		query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomContratante,  ");
		query.append("IFNULL(CONCAT(dom.REF_CALLE,' ',dom.NUM_EXTERIOR,' ',dom.REF_COLONIA),'') AS domContratante,  ");
		query.append("date_format(os.FEC_ALTA,'" + formatoFecha + "') AS fechaPago, '" +usuario + "' AS nomUsuario  ");
		query.append("FROM SVC_ORDEN_SERVICIO os  ");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE)  ");
		query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA)  ");
		query.append("JOIN SVT_DOMICILIO dom ON (con.ID_DOMICILIO = dom.ID_DOMICILIO)  ");
		query.append("JOIN SVT_PAGO_BITACORA pb ON (os.ID_ORDEN_SERVICIO = pb.ID_REGISTRO AND pb.ID_FLUJO_PAGOS = 1)  ");
		query.append("LEFT JOIN SVT_PAGO_DETALLE pd ON (pb.ID_PAGO_BITACORA = pd.ID_PAGO_BITACORA)  ");
		query.append("WHERE pb.CVE_ESTATUS_PAGO IN (2, 8)  ");
		query.append("AND os.ID_ORDEN_SERVICIO = " + this.idODS);

		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	 public DatosRequest actualizaPB() throws UnsupportedEncodingException {
		    DatosRequest request = new DatosRequest();
			Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("UPDATE SVT_PAGO_BITACORA");
	        q.agregarParametroValues("FEC_ACTUALIZACION", "NOW()");
	        q.agregarParametroValues("ID_USUARIO_MODIFICA", this.getIdUsuarioModifica().toString());
	        q.agregarParametroValues("IND_GEN_PAGARE", "0");
	        q.addWhere("ID_REGISTRO = " + this.idODS);
	        q.addWhere("AND ID_FLUJO_PAGOS = 1");

	        String query = q.obtenerQueryActualizar();
			log.info(query);
	        String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			
			return request;
    }

	public DatosRequest crearPagare() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_PAGARE");
		q.agregarParametroValues("ID_ODS", "" + this.idODS);
		q.agregarParametroValues("TIM_HORA", "TIME (NOW())");
		q.agregarParametroValues("IMP_PAGO", "" + this.importe);
		q.agregarParametroValues("NUM_REDITO", "" + this.redito);
		q.agregarParametroValues("FEC_ALTA", "DATE (NOW())");
		q.agregarParametroValues("NUM_CANTIDAD", "''");
		q.agregarParametroValues("ID_USUARIO_ALTA", "'" + this.idUsuarioAlta + "'");
		q.agregarParametroValues("REF_FIRMA", "''");
		
		String query = q.obtenerQueryInsertar();
		log.info(query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
	}
	
	 public Map<String, Object> imprimirNotaRem(FormatoPagareDto formatoDto,String nombrePdfNotaRem){
		Map<String, Object> envioDatos = new HashMap<>();
		envioDatos.put("folioPagare", formatoDto.getFolioPagare());
		envioDatos.put("importe", "$" + formatoDto.getImporte());
		envioDatos.put("cantidad", formatoDto.getCantidad());
		envioDatos.put("folioODS", formatoDto.getFolioPagare());
		envioDatos.put("fechaODS", formatoDto.getFechaODS());
		envioDatos.put("redito", formatoDto.getRedito());
		envioDatos.put("nomContratante", formatoDto.getNomContratante());
		envioDatos.put("domContratante", formatoDto.getDomContratante());
		envioDatos.put("fechaPagare", formatoDto.getFechaPago());
		envioDatos.put("nomUsuario", formatoDto.getNomUsuario());
		envioDatos.put("tipoReporte", formatoDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfNotaRem);
		envioDatos.put("horaODS", formatoDto.getHoraODS());
		
		return envioDatos;
	 }
	
}
