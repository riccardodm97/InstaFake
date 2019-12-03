package it.uniroma3.instaService;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.logic.InstaConfig;

@Service
public class AuthService {
	
	@Autowired 
	InstaConfig instaconf;
	
	public void Log() {
		Instagram4j instagram =instaconf.config();
		try {
			if(instagram.login().getStatus().equalsIgnoreCase("ok")){
				System.out.println("[Login effettuato con successo]");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}


