package it.uniroma3.analytics;

import org.springframework.beans.factory.annotation.Autowired;

import it.uniroma3.model.ResearchStats;
import it.uniroma3.service.ResearchStatsService;


public class FinalResult {

	@Autowired 
	private ResearchStatsService researchService;

	@Autowired 
	private InstaMetrics metrics;


	public void printResults(String username) {

		ResearchStats rs=this.researchService.trovaPerUsername(username);

		System.out.println("\nRisultati della ricerca su: "+username+"\n");

		System.out.println("numero di like medio per post: "+rs.getAvgLike_count());

		double like_variation=this.metrics.LFR_an(rs.getLfr(), rs.getFollowers_count());

		System.out.println("questo valore è");
		if(like_variation>0) {
			System.out.println("più bassso del "+(int)like_variation*100+"% rispetto alla media di account simili\n");
			if(like_variation>=0.4) {
				System.out.println("una variazione superiore al 40% può indicare un'interazione bassa dei follower\n");
			}
		}
		else {
			System.out.println("più alto del "+((int)Math.abs(like_variation))*100+"% rispetto alla media di account simili\n");
		}

		System.out.println("\n");
		
		
		System.out.println("nei post in media per ogni commento ci sono "+rs.getAvgLtc_ratio()+"like\n");
		
		System.out.println("\n");
		
		System.out.println("numero di commenti medio per post: "+rs.getAvgComments_count());

		double comm_variation=this.metrics.CFR_an(rs.getCfr(), rs.getFollowers_count());

		System.out.println("questo valore è");
		if(comm_variation>0) {
			System.out.println("più bassso del "+(int)comm_variation*100+"% rispetto alla media di account simili\n");
			if(comm_variation>=0.4) {
				System.out.println("una variazione superiore al 40% può indicare un'interazione bassa dei follower\n");
			}
		}
		else {
			System.out.println("più alto del "+((int)Math.abs(comm_variation))*100+"% rispetto alla media di account simili\n");
		}
		
		System.out.println("inoltre il numero di commenti probabilmente fake rilevati è: "+rs.getFakeComments_count()+"\n");
		System.out.println("con una media di "+rs.getAvgFakeComments_count()+"commenti fake per post\n");
		
		
		System.out.println("\n");

		double avg_hashtag=rs.getAvgHashtag_count();
		
		System.out.println("numero di hashtag medio per post: "+avg_hashtag+"\n");
		if(metrics.too_many_hashtag(avg_hashtag)) {
			System.out.println("questo numero è superiore alla media di 3.5 hashtag per post di account simili,"
					+ "e può indicare un tentativo di di incrementare la visibilità e e le interazioni in maniera");
		}
		
		System.out.println("il numero di account sospetti che questo utente segue è: "+rs.getSuspect_following_count()+"\n");
		
		System.out.println("il numero di account sospetti che seguono questo utente è: "+rs.getSuspect_followers_count()+"\n");
		
		System.out.println("\n");
		
		


	}
}
