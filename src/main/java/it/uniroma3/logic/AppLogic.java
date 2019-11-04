package it.uniroma3.logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.uniroma3.analytics.DataAnalysis;
import it.uniroma3.service.AuthService;
import it.uniroma3.service.DataService;
import it.uniroma3.service.ProfileSubjectService;

@Component
public class AppLogic {

	@Autowired
	private ProfileSubjectService profileService;

	@Autowired
	private AuthService authservice;

	@Autowired
	private DataService dataservice;

	@Autowired
	private DataAnalysis dataAnalysis;


	public void Start() throws Exception {
		System.out.println("Effettuo il login...");

		//effettuo il login
		this.authservice.Log();

		//prendo in input l'username sul quale condurre la ricerca

		System.out.println("su quale profilo(username) vuoi eseguire la ricerca?\n");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String line= br.readLine();
		//br.close();


		//possibilità di eseguire solo l'analisi (es: stessa ricerca metriche diverse)

		if(this.profileService.esiste(line)) {
			System.out.println("su questo utente esiste una precendente ricerca\n"
					+ "premere 1 per rieseguire la ricerca\n"
					+ "premere 2 per eseguire solo l'analisi");
			//BufferedReader br2=new BufferedReader(new InputStreamReader(System.in));
			String scelta= br.readLine();
			br.close();

			switch(scelta) {
			case "1":
				this.dataservice.Search(line);                                      //inizio la ricerca dell'account scelto
				this.dataAnalysis.StartDataAnalysis(line);                          //analizzo i dati ottenuti
				break;
			case "2":
				this.dataAnalysis.StartDataAnalysis(line);                          //analizzo i dati ottenuti
				break;
			default:
				System.out.println("eseguo solo l'analisi");
				this.dataAnalysis.StartDataAnalysis(line);		
			}
		}
		
		else {
			br.close();
			
			this.dataservice.Search(line);                                      //inizio la ricerca dell'account scelto
			
			this.dataAnalysis.StartDataAnalysis(line);                          //analizzo i dati ottenuti
			
		}

		//faccio qualcosa con i risultati

	}


}
