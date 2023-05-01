package com.imss.sivimss.formatopagare.util;

public class ConvertirImporteLetra {
	
	public static String importeEnTexto(int iImporte ) {
		String strImporte;
		// Obtiene unidad
		int iUnidad = iImporte%10;
		iImporte = iImporte/10;		
		strImporte = ConvertirImporteLetra.unidadEnTexto(iUnidad);
		
		// Obtiene decena
		int iDecena = iImporte%10;
		iImporte = iImporte/10;
		if ((iUnidad==0) && (iDecena>0))
			strImporte = decenaEnTexto(iDecena);
		else if (iDecena==1)
			strImporte = decenas(10+iUnidad);
		else if (iDecena > 1)
			strImporte = decenaEnTexto(iDecena) + " y " + strImporte;
		
		// Obtiene centena
		int iCentena = iImporte%10;
		if ((iCentena!=1) && (iCentena!=5) && (iCentena!=9) && (iCentena!=0))
			strImporte = unidadEnTexto(iCentena) + "cientos" + " " + strImporte;
		else if ((iCentena==1) || (iCentena==5) || (iCentena==9))
			strImporte = centenaEnTexto(iCentena) + " " + strImporte;
		
		return strImporte;
	}
	
	private static String unidadEnTexto(int iNumero){
		 switch(iNumero){
			case 1:
				return "uno";
			case 2:
				return "dos";
			case 3:
				return "tres";
			case 4:
				return "cuatro";
			case 5:
				return "cinco";
			case 6:
				return "seis";
			case 7:
				return "siete";
			case 8:
				return "ocho";
			case 9:
				return "nueve";
			case 0:
				return "cero";
			default:
				return "";
		 }
	}
	
	private static String decenaEnTexto(int iDecena){
		  switch (iDecena){
			case 1:
				return "diez";
			case 2:
				return "veinte";
			case 3:
				return "treinta";
			case 4:
				return "cuarenta";
			case 5:
				return "cincuenta";
			case 6:
				return "sesenta";
			case 7:
				return "setenta";
			case 8:
				return "ochenta";
			case 9:
				return "noventa";		
			default:
				return "";
		  }
	}

	private static String decenas(int iDecena) {
		  switch (iDecena){
			case 11:
				return "once";
			case 12:
				return "doce";
			case 13:
				return "trece";
			case 14:
				return "catorce";
			case 15:
				return "quince";
			case 16:
				return "dieciseis";
			case 17:
				return "diecisiete";
			case 18:
				return "dieciocho";
			case 19:
				return "diecinueve";		
			default:
				return "";
		  }
	}
	
	private static String centenaEnTexto(int iCentena){
		  switch (iCentena){
			case 1:
				return "ciento";
			case 2:
				return "doscientos";
			case 3:
				return "trescientos";
			case 4:
				return "cuatrocientos";
			case 5:
				return "quinientos";
			case 6:
				return "seiscientos";
			case 7:
				return "setecientos";
			case 8:
				return "ochocientos";
			case 9:
				return "novecientos";				
			default:
				return "";
		  }
	}
	
}
