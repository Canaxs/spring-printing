package com.print;

import com.print.enums.TemplateType;
import com.print.persistence.entity.TemplateTable;
import com.print.persistence.repository.TemplateRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PrintApplicationTests {

	@Mock
	private TemplateRepository templateRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void createTestTemplate() {
	}

}
