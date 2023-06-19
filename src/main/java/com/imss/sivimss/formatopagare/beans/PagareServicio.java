package com.imss.sivimss.formatopagare.beans;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.formatopagare.util.AppConstantes;
import com.imss.sivimss.formatopagare.model.request.BusquedaDto;
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

	private Integer id;
	private Integer idODS;
	private Integer importe;
	private Integer redito;
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
	
	public DatosRequest detallPagare(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT os.CVE_FOLIO AS folioODS, DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') AS fechaODS, \n");
		query.append("TIME(os.FEC_ALTA) AS hora, IFNULL(pd.IMP_IMPORTE,0) AS importe, 6.0 AS redito, os.CVE_FOLIO AS folioPagare, \n");
		query.append("CONCAT(prc.NOM_PERSONA,' ',prc.NOM_PRIMER_APELLIDO,' ',prc.NOM_SEGUNDO_APELLIDO) AS nomContratante, \n");
		query.append("IFNULL(CONCAT(dom.DES_CALLE,' ',dom.NUM_EXTERIOR,' ',dom.DES_COLONIA),'') AS domContratante, \n");
		query.append("date_format(os.FEC_ALTA,'" + formatoFecha + "') AS fechaPago, \n");
		query.append("CONCAT(usu.NOM_USUARIO,' ',usu.NOM_APELLIDO_PATERNO,' ',usu.NOM_APELLIDO_MATERNO) AS nomUsuario \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_CONTRATANTE con ON (os.ID_CONTRATANTE = con.ID_CONTRATANTE) \n");
		query.append("JOIN SVC_PERSONA prc ON (con.ID_PERSONA = prc.ID_PERSONA) \n");
		query.append("JOIN SVT_DOMICILIO dom ON (con.ID_DOMICILIO = dom.ID_DOMICILIO) \n");
		query.append("LEFT JOIN SVT_PAGO_BITACORA pb ON (os.ID_ORDEN_SERVICIO = pb.ID_FLUJO_PAGOS) \n");
		query.append("LEFT JOIN SVT_PAGO_DETALLE pd ON (pb.ID_PAGO_BITACORA = pd.ID_PAGO_BITACORA) \n");
		query.append("JOIN SVT_USUARIOS usu ON (os.ID_USUARIO_ALTA = usu.ID_USUARIO) \n");
		query.append("WHERE os.ID_ORDEN_SERVICIO = " + this.idODS);
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest crearPagare() throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_PAGARE");
		q.agregarParametroValues("ID_ODS", "" + this.idODS);
		q.agregarParametroValues("TIM_HORA", "TIME (NOW())");
		q.agregarParametroValues("IMP_PAGO", "" + this.importe);
		q.agregarParametroValues("NUM_REDITO", "" + this.redito);
		q.agregarParametroValues("FEC_ALTA", "DATE (NOW())");
		q.agregarParametroValues("DES_CANTIDAD", "''");
		q.agregarParametroValues("ID_USUARIO_ALTA", "'" + this.idUsuarioAlta + "'");
		
		String query = q.obtenerQueryInsertar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
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
		
		return envioDatos;
	 }
	
}
