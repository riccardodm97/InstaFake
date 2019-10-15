package it.uniroma3.logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.uniroma3.service.AuthService;
import it.uniroma3.service.DataService;

@Component
public class AppLogic {
	
	@Autowired
	private AuthService authservice;
	
	@Autowired
	private DataService dataservice;
	
	
	public void Start() throws Exception {
		System.out.println("Effettuo il login...");
		
		//effettuo il login
		this.authservice.Log();
		
		System.out.println("su quale profilo vuoi eseguire la ricerca?\n");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String line= br.readLine();
		
		//inizio la ricerca dell'account scelto
		
		this.dataservice.Search(line);
		
		
		
	}
	

}
