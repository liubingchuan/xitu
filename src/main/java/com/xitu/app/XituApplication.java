package com.xitu.app;

import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@ServletComponentScan
public class XituApplication {

	public static void main(String[] args) {

//		SpringApplication.run(XituApplication.class, args);
		
		
		Pattern pattern = Pattern.compile("\\d{6}");
		boolean matches = pattern.matcher("asdf121109").matches();
		System.out.println(matches);

	}

}
