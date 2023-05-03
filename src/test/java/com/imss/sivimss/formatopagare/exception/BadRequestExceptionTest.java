package com.imss.sivimss.formatopagare.exception;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.imss.sivimss.formatopagare.util.UtUtils.createInstance;
import static com.imss.sivimss.formatopagare.util.UtUtils.setField;
import static org.junit.Assert.assertNull;

public final class BadRequestExceptionTest {
    @Test
    public void testGetEstado_ReturnCodigo() throws Exception {
        BadRequestException badRequestException = ((BadRequestException) createInstance("com.imss.sivimss.formatopagare.exception.BadRequestException"));
        setField(badRequestException, "com.imss.sivimss.formatopagare.exception.BadRequestException", "codigo", null);

        HttpStatus actual = badRequestException.getEstado();

        assertNull(actual);
    }

    @Test
    public void testGetMensaje_ReturnMensaje() throws Exception {
        BadRequestException badRequestException = ((BadRequestException) createInstance("com.imss.sivimss.formatopagare.exception.BadRequestException"));
        setField(badRequestException, "com.imss.sivimss.formatopagare.exception.BadRequestException", "mensaje", null);

        String actual = badRequestException.getMensaje();

        assertNull(actual);
    }
}
