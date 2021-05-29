/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignment.thesociopath;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 *
 * @author XuanEr
 */
@Configuration
@EnableWebFlux
public class AppConf implements WebFluxConfigurer {
	
	@Override
	public void addCorsMappings(CorsRegistry corsRegistry){
		corsRegistry.addMapping("/**")
			.allowedOrigins("http://localhost")
			.allowedMethods("POST");
	}
}