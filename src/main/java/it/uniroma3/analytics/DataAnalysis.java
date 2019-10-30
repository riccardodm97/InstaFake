package it.uniroma3.analytics;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.jfasttext.JFastText;

import it.uniroma3.model.Comment;
import it.uniroma3.model.InstagramUserDB;
import it.uniroma3.model.Media;
import it.uniroma3.model.ProfileSubject;
import it.uniroma3.model.ResearchStats;
import it.uniroma3.service.CommentService;
import it.uniroma3.service.MediaService;
import it.uniroma3.service.ProfileSubjectService;
import it.uniroma3.service.ResearchStatsService;

@Component
public class DataAnalysis {
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private MediaService mediaService;
	
	@Autowired
	private TextPreprocessing txtProcess;
	
	@Autowired 
	private ResearchStatsService researchService;
	
	@Autowired
	private ProfileSubjectService profileService;
	
	private JFastText jft;           //libreria per il machine learning sui commenti
	
	private ResearchStats rs;        //classe per i risultati dell'analisi
	
	private ProfileSubject ps;       //profilo su cui si è basata la ricerca
	
	public void StartDataAnalysis(String user) {
		
		//ricerco il profilo di cui ho estratto i dati 
		this.ps=this.profileService.cercaPerUsername(user);
		
		//inizializzo la classe dei risultati
		this.rs=new ResearchStats(user);
		
		//analizzo i following del profilo
		this.FollowingAnalysis();
		
		//analizzo i follower del profilo
		this.FollowersAnalysis();
		
		this.StudyMediaData();
		
		//salvo nel db i dati analizzati
		this.researchService.inserisci(rs);
	}
	
	//per determinare numero medio di : like , commenti e hashtag 
	public void StudyMediaData() {
		List<Media> posts=this.mediaService.getAllMediaByOwnerUser(this.rs.getUsername());
		int num_comm=0;
		int num_likes=0;
		int num_hashtag=0;
		int num_posts=posts.size();
		for(Media m: posts) {
			List<Comment> commenti=m.getComments();
			num_comm+=commenti.size();
			num_likes+=m.getNum_likes();
			num_hashtag+=this.extractAndCountHashtag(m.getCaption());
			this.AnalyzeComments(commenti);                            //analizzo i commenti estratti per post
			commenti.clear();
		}
		this.rs.setPost_count(num_posts);
		this.rs.setAvgComments_count(num_comm/num_posts);
		this.rs.setAvgLike_count(num_likes/num_posts);
		this.rs.setAvgHashtag_count(num_hashtag/num_posts);
	}
	
	public int extractAndCountHashtag(String text) {
		return StringUtils.countOccurrencesOf(text, "#");
	}
	
	
	public void FollowingAnalysis() {
		int fake_following=0;
		List<InstagramUserDB> following=this.ps.getFollowing();
		for(InstagramUserDB user: following) {
			double value=this.AnalyzeUser(user);
			if(value>=0.6) fake_following+=1;
			value=0;
		}
		this.rs.setSuspect_following_count(fake_following);
	}
	
	public void FollowersAnalysis() {
		int fake_followers=0;
		List<InstagramUserDB> followers=this.ps.getFollowers();
		for(InstagramUserDB user: followers) {
			double value=this.AnalyzeUser(user);
			if(value>=0.6) fake_followers+=1;
			value=0;
		}
		this.rs.setSuspect_followers_count(fake_followers);
	}
	
	public double AnalyzeUser(InstagramUserDB user) {
		double value=0;
		if(user.isVerified()) return 0;    //se l'account è verificato lo considero comunque genuino
		if(user.getNum_following()!=0) {
			double ratio=user.getNum_followers()/user.getNum_following();
			if(user.getNum_followers()<=2000 && ratio<=0.5) value+=0.1;
		}
		if(user.getNum_following()>=1500 && user.getNum_following()<3000) value+=0.05;
		if(user.getNum_following()>=3000) value+=0.1;
		if(user.getNum_posts()<=5) value+=0.1;
		if(user.getNum_posts()>5 && user.getNum_posts()<=10) value+=0.05;
		if(user.has_anonymous_profile_pic()) value+=0.2;
		if(!StringUtils.hasText(user.getBio())) value+=0.15;
		if(user.isPrivate()) value+=0.025;
		if(user.getNum_tags()<=5) value+=0.05;
		return value;
	}
	
	
	//determinare quanti commenti fake ci sono 
	
	
	public void AnalyzeComments(List<Comment> commenti) {
		this.jft=new JFastText();    //inizializzo la classe della libreria da usare per la classification del testo
		this.jft.loadModel("src/main/resources/models/model.bin");    //carico il modello trainato
		for(Comment c:commenti) {
			double prob=this.TextClassification(c.getText());     //per ogni commento setto il livello di attendibilità 
			c.setTrustworthiness(prob);
			this.commentService.inserisci(c);
		}
	
	}
	
	public double TextClassification(String text) {
		//preprocessing
		String processed_text= txtProcess.Process(text); 
		//System.out.println(processed_text);
		JFastText.ProbLabel probLabel = jft.predictProba(processed_text);     //predizione della label 
		double prob=Math.exp(probLabel.logProb);
		System.out.printf("\nThe label of '%s' is '%s' with probability %f\n",
		        processed_text, probLabel.label, Math.exp(probLabel.logProb));
		//if(probLabel.label.equals("__label__false"))                        //se dalla label risulta che il commento è proabilmente fake
			//prob=1-prob;                                                      //allora il valore di attendibilità è 1-prob che sia falso     
		return prob;
	}
}
