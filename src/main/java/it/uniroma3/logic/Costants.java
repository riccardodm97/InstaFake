package it.uniroma3.logic;

import org.springframework.stereotype.Component;

@Component
public class Costants {
	
	//inserire username e password di un account instagram esistente a cui si ha accesso
	
	private final String username="user";          //rentit.infoapp
	private final String password="password";       //RentXApp00.
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	
	
}
