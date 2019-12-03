package it.uniroma3.logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.uniroma3.analytics.DataAnalysis;
import it.uniroma3.analytics.FinalResult;
import it.uniroma3.instaService.AuthService;
import it.uniroma3.instaService.DataService;

@Component
public class AppLogic {

	@Autowired
	private AuthService authservice;

	@Autowired
	private DataService dataservice;

	@Autowired
	private DataAnalysis dataAnalysis;
	
	@Autowired
	private FinalResult finalResult;


	public void Start() throws Exception {

		//prendo in input l'username sul quale condurre la ricerca
		
		System.out.println("ciao,\n"
				+ "premi 1 per ottenere i dati di un account\n"
				+ "premi 2 per effettuare l'analisi di una precedente ricerca\n");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String scelta= br.readLine();
		
		
		String account=null;
		
		switch(scelta) {
		case "1":
			System.out.println("di quale profilo(username) vuoi ottentere i dati?\n");
			account= br.readLine();
			
			System.out.println("[Effettuo il login...]");
			this.authservice.Log();                                                    //effettuo il login
			
			this.dataservice.Search(account);                                          //inizio la ricerca dell'account scelto
			
			br.close();                                                                //spostarlo prima? problema non terminazione app
			break;
		
		case "2":
			System.out.println("di quale profilo(username) vuoi eseguire l'analisi i dati?\n");
			account= br.readLine();
			
			this.dataAnalysis.StartDataAnalysis(account);                          //analizzo i dati precedentemente ottenuti
			
			br.close();
		
			finalResult.printResults(account);                                      //stampo i risultati ottenuti
			break;
			
		default:
			
			System.out.println("[invalid input]\n");
			br.close();
			return ;
			
		}
	
		
		return ;
		
	}

}
