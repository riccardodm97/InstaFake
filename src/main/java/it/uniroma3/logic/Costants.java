package it.uniroma3.logic;

import org.springframework.stereotype.Component;

@Component
public class Costants {
	
	private final String username="rentit.infoapp";
	private final String password="RentXApp00.";
	
	private final int num_cicli=5;
	private final boolean num_cicli_valido=false;
	
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public int getNum_cicli() {
		return num_cicli;
	}
	public boolean isNum_cicli_valido() {
		return num_cicli_valido;
	}
	
}
