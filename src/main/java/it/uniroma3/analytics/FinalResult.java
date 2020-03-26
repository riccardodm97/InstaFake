package it.uniroma3.analytics;

import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.uniroma3.model.ResearchStats;
import it.uniroma3.service.ResearchStatsService;

@Component
public class FinalResult {

	@Autowired
	private ResearchStatsService researchService;

	@Autowired 
	private InstaMetrics metrics;


	public void printResults(String username) {
		
		ResearchStats rs=this.researchService.trovaPerUsername(username);
		
		DecimalFormat f= new DecimalFormat("0.00");

		System.out.println("\nRisultati della ricerca su: "+username+"\n");

		System.out.println("numero di like medio per post: "+ f.format(rs.getAvgLike_count()));

		double like_variation=this.metrics.LFR_an(rs.getLfr(), rs.getFollowers_count());

		if(like_variation>0) {
			System.out.println("questo valore è più basso del "+ f.format(like_variation*100) + "% rispetto alla media di account simili");
			if(like_variation>=0.4) {
				System.out.println("una variazione superiore al 40% può indicare un'interazione bassa dei propri follower");
			}
		}
		else {
			System.out.println("questo valore è più alto del "+ f.format(Math.abs(like_variation*100)) + "% rispetto alla media di account simili");
		}

		System.out.println("\n");

		System.out.println("nei post in media per ogni commento ci sono "+ f.format(rs.getAvgLtc_ratio()) + " like");

		System.out.println("\n");

		System.out.println("il numero di commenti medio per post è: "+ f.format(rs.getAvgComments_count()));

		double comm_variation=this.metrics.CFR_an(rs.getCfr(), rs.getFollowers_count());

		if(comm_variation>0) {
			System.out.println("questo valore è più basso del "+ f.format(comm_variation*100) +"% rispetto alla media di account simili\n");
			if(comm_variation>=0.4) {
				System.out.println("una variazione superiore al 40% può indicare un'interazione bassa dei follower\n");
			}
		}
		else {
			System.out.println("questo valore è più alto del "+ f.format(Math.abs(comm_variation*100)) +"% rispetto alla media di account simili\n");
		}

		System.out.println("inoltre il numero di commenti probabilmente fake rilevati è: " + rs.getFakeComments_count()+"\n");
		System.out.println("con una media di "+ f.format(rs.getAvgFakeComments_count()) + " commenti fake per post");


		System.out.println("\n");

		double avg_hashtag=rs.getAvgHashtag_count();

		System.out.println("il numero di hashtag medio per post è di: " + f.format(avg_hashtag) + "\n");
		if(metrics.too_many_hashtag(avg_hashtag)) {
			System.out.println("questo numero è superiore alla media di 3.5 hashtag per post di account simili,"
					+ "e può indicare un tentativo di di incrementare la visibilità e le interazioni in maniera artificiosa\n");
		}


		System.out.println("il numero di account che seguono l'utente è: " + rs.getFollowers_count());

		System.out.println("di questi il numero di account sospetti è: "+ rs.getSuspect_followers_count());
		
		double fake_tot_perc=rs.getSuspect_followers_count()/ (double) rs.getFollowers_count();
		
		System.out.println("ovvero il " + f.format(fake_tot_perc*100) +"% del totale");

		if(fake_tot_perc>0.15) {
			System.out.println("maggiore rispetto alla media del 10/15 %");
		}
		
		System.out.println("\n");

		System.out.println("il numero di account che l'utente segue è: " + rs.getFollowing_count());

		System.out.println("di questi il numero di account sospetti è: " + rs.getSuspect_following_count());

		System.out.println("\n");

		int isSuspect_following=this.metrics.following_limit(rs.getFollowers_count(), rs.getFollowing_count());

		if(isSuspect_following==1) {
			
			System.out.println("il numero di account che questo utente segue è maggiore della media per account simili"
					+ "il che spesso indica un tentativo di incremento artificiale delle proprie analytics\n");
		}
		
		else if(isSuspect_following==2) {
			
			System.out.println("il numero di account che questo utente segue è molto maggiore rispetto alla media per account simili"
					+ "il che indica un tentativo di incremento artificiale delle proprie analytics\n");
		}
		
		System.out.println("la follower to following ratio è pari al: " + f.format(rs.getFfr()*100) + "%");
		
		if(rs.getFfr()<=1.5)  System.out.println("più bassa della media, il che può indicare un tentativo di incremento"
				+ "artificiale e non omogeneo dei followers tramite meccanisimi come il follow/unfollow\n");
		
		System.out.println("\n");
		
		System.out.println("il numero di account seguiti da questo utente che hanno il profilo privato e meno di mille followers è di: "+rs.getLess1kfandprivate()+"\n");;
		
		if(rs.getLess1kfandprivate()>=200) {
			
			System.out.println("il fatto di seguire questo tipo di profili indica solitamente che la persona è 'conosciuta' direttamente dall'utente"
					+ "un numero superiore ai 200 non è usuale per il tipo di relazione e può indicare il tentativo"
					+ "di incremento artificiale dei followers\n");
		}
		
		System.out.println("l'engagement rate di questo profilo è del "+ f.format((rs.getEngagement_rate()*100))+"%\n");
		
		if(rs.getEngagement_rate()<=0.01) {
			
			System.out.println("ed è più basso della media per account simili\n");
		}
		




	}
}
