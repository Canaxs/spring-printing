package com.print;

import com.ironsoftware.ironpdf.License;
import org.aspectj.weaver.tools.cache.CacheKeyResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrintApplication implements CommandLineRunner {

	@Value("${ironpdfKEY}")
	private String ironPdfKey;

	public static void main(String[] args) {
		SpringApplication.run(PrintApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		License.setLicenseKey(ironPdfKey);
	}
}
