package com.imss.sivimss.formatopagare.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.formatopagare.util.ProviderServiceRestTemplate;
import com.imss.sivimss.formatopagare.util.Response;
import com.imss.sivimss.formatopagare.model.response.ODSGeneradaResponse;
import com.imss.sivimss.formatopagare.util.LogUtil;
import com.imss.sivimss.formatopagare.model.request.UsuarioDto;
import com.imss.sivimss.formatopagare.exception.BadRequestException;
import com.imss.sivimss.formatopagare.model.request.BusquedaDto;
import com.imss.sivimss.formatopagare.model.request.FormatoPagareDto;
import com.imss.sivimss.formatopagare.model.request.PagareServicioDto;
import com.imss.sivimss.formatopagare.util.AppConstantes;
import com.imss.sivimss.formatopagare.util.ConvertirGenerico;
import com.imss.sivimss.formatopagare.util.DatosRequest;
import com.imss.sivimss.formatopagare.util.MensajeResponseUtil;
import com.imss.sivimss.formatopagare.beans.OrdenServicio;
import com.imss.sivimss.formatopagare.beans.PagareServicio;
import com.imss.sivimss.formatopagare.service.FormatoPagareService;

@Service
public class FormatoPagareServiceImpl implements FormatoPagareService {
	
	@Value("${endpoints.dominio}")
	private String urlDominioGenerico;
	
	private static final String PAGINADO = "/paginado";
	
	private static final String CONSULTA = "/consulta";
	
	private static final String CREAR = "/crear";
	
	private static final String ACTUALIZAR = "/actualizar";
	
	@Value("${endpoints.generico-reportes}")
	private String urlReportes;
	
	@Value("${formato_fecha}")
	private String formatoFecha;
	
	private static final String NOMBREPDFPAGARE = "reportes/generales/FormatoPagare.jrxml";
	
	private static final String NOMBREPDFREPORTE = "reportes/generales/ReporteODSPagare.jrxml";
	
	private static final String INFONOENCONTRADA = "45";
	
	private static final String ERROR_DESCARGA = "64";
	
	private static final String ALTA = "alta";
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private LogUtil logUtil;
	
	private static final Logger log = LoggerFactory.getLogger(FormatoPagareServiceImpl.class);

	@Override
	public Response<?> consultarODS(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		OrdenServicio ordenServicio = new OrdenServicio();
		
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);

		try {
		    return providerRestTemplate.consumirServicio(ordenServicio.obtenerODS(request, busqueda, formatoFecha).getDatos(), urlDominioGenerico + PAGINADO, 
				authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
	        logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
	    }
	}

	@Override
	public Response<?> listadoODS(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		OrdenServicio ordenServicio = new OrdenServicio();
		List<ODSGeneradaResponse> ODSResponse;
		
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		Response<?> response = providerRestTemplate.consumirServicio(ordenServicio.listadoODS(busqueda).getDatos(), urlDominioGenerico + CONSULTA, 
				authentication);
		
		if (response.getCodigo() == 200) {
			ODSResponse = Arrays.asList(modelMapper.map(response.getDatos(), ODSGeneradaResponse[].class));
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(ODSResponse));
		}
		return response;
	}
	
	@Override
	public Response<?> contratante(DatosRequest request, Authentication authentication) throws IOException {
		OrdenServicio ordenServicio = new OrdenServicio();
		
		try {
		    return providerRestTemplate.consumirServicio(ordenServicio.getContratante(request).getDatos(), urlDominioGenerico + CONSULTA, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
	       	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
	    }
	}
	
	@Override
	public Response<?> buscarODS(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		OrdenServicio ordenServicio = new OrdenServicio();
		
		Response<?> response = providerRestTemplate.consumirServicio(ordenServicio.buscarODS(request, busqueda, formatoFecha).getDatos(), urlDominioGenerico + PAGINADO,
				authentication);
		ArrayList datos1 = (ArrayList) ((LinkedHashMap) response.getDatos()).get("content");
		if (datos1.isEmpty()) {
			response.setMensaje(INFONOENCONTRADA);
	    }
		
		return response;
	}

	@Override
	public Response<?> importeLetra(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		
		PagareServicio pagareServicio = new PagareServicio();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		PagareServicioDto pagareDto = gson.fromJson(datosJson, PagareServicioDto.class);
		pagareServicio.setImporte(pagareDto.getImporte());
		
		return new Response<Object>(false, HttpStatus.OK.value(), "Exito" , ConvertirGenerico.convertInstanceOfObject(pagareServicio.importeLetra()));
	}

	@Override
	public Response<?> detallePagare(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		PagareServicioDto pagareDto = gson.fromJson(datosJson, PagareServicioDto.class);
		if (pagareDto.getIdODS() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		PagareServicio pagareServicio = new PagareServicio();
		pagareServicio.setIdODS(pagareDto.getIdODS());
		
		try {
		    return providerRestTemplate.consumirServicio(pagareServicio.detallPagare(request, formatoFecha).getDatos(), urlDominioGenerico + CONSULTA, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
	       	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
	    }
	}

	@Override
	public Response<?> agregarPagare(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		PagareServicioDto pagareDto = gson.fromJson(datosJson, PagareServicioDto.class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		
		PagareServicio pagareServicio = new PagareServicio(pagareDto);
		pagareServicio.setIdUsuarioAlta(usuarioDto.getIdUsuario());
		pagareServicio.setIdUsuarioModifica(usuarioDto.getIdUsuario());
		providerRestTemplate.consumirServicio(pagareServicio.actualizaPB().getDatos(), urlDominioGenerico + ACTUALIZAR, authentication);
		
		try {
			return providerRestTemplate.consumirServicio(pagareServicio.crearPagare().getDatos(), urlDominioGenerico + CREAR, authentication);
		 } catch (Exception e) {
			log.error(e.getMessage());
	       	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), ALTA, authentication);
			return null;
	     }
		
	}

	@Override
	public Response<?> generarPagare(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		FormatoPagareDto formatoPagareDto = gson.fromJson(datosJson, FormatoPagareDto.class);
		PagareServicio pagareServicio = new PagareServicio();
		
		Map<String, Object> envioDatos = pagareServicio.imprimirNotaRem(formatoPagareDto, NOMBREPDFPAGARE);
		Response<?> response = providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);
	
		return MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_DESCARGA);
	}

	@Override
	public Response<?> descargarDocumento(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto reporteDto = gson.fromJson(datosJson, BusquedaDto.class);
		
		Map<String, Object> envioDatos = new OrdenServicio().generarReporte(reporteDto, NOMBREPDFREPORTE, formatoFecha);
		Response<?> response = providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);
		
		return MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_DESCARGA);
	}

}
	