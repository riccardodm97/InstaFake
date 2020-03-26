package it.uniroma3.logic;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class InstaConfig {
	
	@Autowired
	private Costants cost;
	
	@Bean
	@Scope("singleton")
	public Instagram4j config() {
		
		Instagram4j instagram = Instagram4j.builder().username(cost.getUsername()).password(cost.getPassword()).build();
		instagram.setup();
		
		return instagram;
	}
}





