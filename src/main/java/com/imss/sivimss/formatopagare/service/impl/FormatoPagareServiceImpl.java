package com.imss.sivimss.formatopagare.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.formatopagare.util.ProviderServiceRestTemplate;
import com.imss.sivimss.formatopagare.util.Response;
import com.imss.sivimss.formatopagare.model.request.BusquedaDto;
import com.imss.sivimss.formatopagare.model.request.PagareServicioDto;
import com.imss.sivimss.formatopagare.util.AppConstantes;
import com.imss.sivimss.formatopagare.util.ConvertirGenerico;
import com.imss.sivimss.formatopagare.util.DatosRequest;
import com.imss.sivimss.formatopagare.beans.OrdenServicio;
import com.imss.sivimss.formatopagare.beans.PagareServicio;
import com.imss.sivimss.formatopagare.service.FormatoPagareService;

@Service
public class FormatoPagareServiceImpl implements FormatoPagareService {
	
	@Value("${endpoints.dominio-paginado}")
	private String urlGenericoPaginado;
	
	@Value("${endpoints.dominio-consulta}")
	private String urlGenericoConsulta;
	
	@Value("${endpoints.dominio-crear}")
	private String urlGenericoCrear;
	
	@Value("${endpoints.dominio-actualizar}")
	private String urlGenericoActualizar;
	
	@Value("${endpoints.generico-reportes}")
	private String urlReportes;
	
	private static final String nombrePdfNotaRem = "reportes/generales/FormatoPagare.jrxml";
	
	private static final String nombrePdfReportes = "reportes/generales/ReporteODSPagare.jrxml";
	
	private static final String infoNoEncontrada = "No se encontró información relacionada a tu búsqueda.";
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;

	@Override
	public Response<?> consultarODS(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		OrdenServicio ordenServicio = new OrdenServicio();
		
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);

		return providerRestTemplate.consumirServicio(ordenServicio.obtenerODS(request, busqueda).getDatos(), urlGenericoPaginado, 
				authentication);
	}

	@Override
	public Response<?> buscarODS(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		OrdenServicio ordenServicio = new OrdenServicio();
		
		Response<?> response = providerRestTemplate.consumirServicio(ordenServicio.buscarODS(request, busqueda).getDatos(), urlGenericoPaginado,
				authentication);
		ArrayList datos1 = (ArrayList) ((LinkedHashMap) response.getDatos()).get("content");
		if (datos1.isEmpty()) {
			response.setMensaje(infoNoEncontrada);
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
		PagareServicio pagareServicio = new PagareServicio();
		
		return providerRestTemplate.consumirServicio(pagareServicio.detallPagare(request).getDatos(), urlGenericoConsulta, authentication);
	}

	@Override
	public Response<?> generarPagare(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<?> descargarPagare(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<?> descargarDocumento(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
	