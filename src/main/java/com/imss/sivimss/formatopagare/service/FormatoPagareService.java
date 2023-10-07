package com.imss.sivimss.formatopagare.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.formatopagare.util.DatosRequest;
import com.imss.sivimss.formatopagare.util.Response;

public interface FormatoPagareService {
	
	Response<?> consultarODS(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> listadoODS(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> contratante(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> buscarODS(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> importeLetra(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> detallePagare(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> agregarPagare(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> generarPagare(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> descargarDocumento(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> buscarContratante(DatosRequest request, Authentication authentication) throws IOException;

}
