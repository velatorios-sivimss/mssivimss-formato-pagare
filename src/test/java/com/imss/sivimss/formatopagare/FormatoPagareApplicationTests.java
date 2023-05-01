package com.imss.sivimss.formatopagare;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.imss.sivimss.formatopagare.FormatoPagareApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FormatoPagareApplicationTests {

	@Test
	void contextLoads() {
		String result="test";
		FormatoPagareApplication.main(new String[]{});
		assertNotNull(result);
	}

}
