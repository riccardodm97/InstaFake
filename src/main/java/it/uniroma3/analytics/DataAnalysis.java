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
		
		//analizzo i post estratti e i relativi commenti
		this.StudyMediaData();
		
		//conto il numero di account privati con meno di 1k followers che l'utente segue
		this.less1kprivateFollowing();
		
		//salvo nel db i dati analizzati
		this.researchService.inserisci(rs);
	}
	
	//per determinare numero medio di : like , commenti e hashtag 
	public void StudyMediaData() {
		List<Media> posts=this.mediaService.getAllMediaByOwnerUser(this.rs.getUsername());
		int num_comm=0;
		int num_likes=0;
		int num_hashtag=0;
		int num_tot_fake=0;
		int num_posts=posts.size();
		for(Media m: posts) {
			List<Comment> commenti=m.getComments();
			num_likes+=m.getNum_likes();
			num_hashtag+=this.extractAndCountHashtag(m.getCaption());
			if(commenti!=null) {
				num_comm+=commenti.size();
				num_tot_fake+=this.AnalyzeComments(commenti);               //analizzo i commenti estratti per post     
			}
			commenti.clear();
		}
		this.rs.setPost_count(num_posts);
		this.rs.setAvgComments_count(num_comm/num_posts);
		this.rs.setAvgLike_count(num_likes/num_posts);
		this.rs.setAvgHashtag_count(num_hashtag/num_posts);
		if(num_comm>0) this.commentsDataProcess(num_tot_fake,num_comm);
		this.engagementRate_calc(num_likes, num_comm, num_posts);
	}
	
	
	//conto il numero di hashtag per caption di un post
	public int extractAndCountHashtag(String text) {
		return StringUtils.countOccurrencesOf(text, "#");
	}
	
	public void engagementRate_calc(int tot_likes_count,int tot_comm_count,int post_count) {
		double er=0;
		int followers_count=this.ps.getProfile().getNum_followers();
		if(followers_count==0) followers_count=1;
		er=((tot_comm_count+tot_likes_count)/post_count)/followers_count;
		this.rs.setEngagement_rate(er);
	}
	
	public void commentsDataProcess(int num_fake,int num_comm) {
		this.rs.setFakeComments_count(num_fake);
		this.rs.setAvgFakeComments_count(num_fake/(double) this.rs.getPost_count());
		this.rs.setFaketotComment_ratio(num_fake/(double)num_comm);
	}
	
	public void FollowingAnalysis() {
		int fake_following=0;
		List<InstagramUserDB> following=this.ps.getFollowing();
		for(InstagramUserDB user: following) {
			double value=this.AnalyzeUser(user);
			if(value>=0.65) {                                                      //soglia del 65 % di suspect
				fake_following+=1;
				System.out.println(user.getUsername());
			}
			value=0;
		}
		this.rs.setSuspect_following_count(fake_following);
	}
	
	public void FollowersAnalysis() {
		int fake_followers=0;
		List<InstagramUserDB> followers=this.ps.getFollowers();
		for(InstagramUserDB user: followers) {
			double value=this.AnalyzeUser(user);
			if(value>=0.65) fake_followers+=1;                                     //mettere una soglia diversa????
			value=0;
		}
		this.rs.setSuspect_followers_count(fake_followers);
	}
	
	public double AnalyzeUser(InstagramUserDB user) {
		double value=0;
		if(user.isVerified()) return 0;    //se l'account è verificato lo considero comunque genuino
		if(user.getNum_following()!=0) {
			double ratio=user.getNum_followers()/user.getNum_following();
			if(user.getNum_followers()<=2000 && ratio<=0.4) value+=0.1;
		}
		if(user.getNum_following()>=1500 && user.getNum_following()<3000) value+=0.1;
		if(user.getNum_following()>=3000) value+=0.2;
		if(user.getNum_posts()<=5) value+=0.15;
		if(user.getNum_posts()>5 && user.getNum_posts()<=10) value+=0.05;
		if(user.has_anonymous_profile_pic()) value+=0.3;
		if(!StringUtils.hasText(user.getBio())) value+=0.2;
		if(user.isPrivate()) value+=0.05;
		if(!user.isPrivate() && user.getNum_tags()<=5) value+=0.1;
		return value;
	}
	
	public void less1kprivateFollowing() {
		int numero=0;
		for(InstagramUserDB user:this.ps.getFollowing()) {
			if(user.getNum_followers()<=1000 && user.isPrivate()) numero++;
		}
		this.rs.setFollow_less1kf(numero);
	}
	
	
	//determino quanti commenti fake ci sono 

	public int AnalyzeComments(List<Comment> commenti) {
		int num_fake_per_post=0;        //numero di commenti ritenuti falsi (sopra il 70% prob)
		
		this.jft=new JFastText();    //inizializzo la classe della libreria da usare per la classification del testo
		this.jft.loadModel("src/main/resources/models/model.bin");    //carico il modello trainato
		for(Comment c:commenti) {
			double prob=this.TextClassification(c.getText());     //per ogni commento setto il livello di attendibilità 
			c.setFalse_prob(prob);
			this.commentService.inserisci(c);
			if(prob>=0.75) num_fake_per_post+=1;                   //soglia del 75% di suspect
		}
		return num_fake_per_post;
	}
	
	public double TextClassification(String text) {
		//preprocessing del testo
		String processed_text= txtProcess.Process(text); 
		double prob;
		List<JFastText.ProbLabel> probLabelList = jft.predictProba(processed_text,2);     //predizione della label 
		
		//inserisco il valore della label false
		if(probLabelList.get(0).label.equals("__label__false")) prob=Math.exp(probLabelList.get(0).logProb);
		else prob=Math.exp(probLabelList.get(1).logProb);
		/*System.out.printf("\nThe label of '%s' is '%s' with probability %f\n",
		        processed_text, probLabelList.get(0).label, Math.exp(probLabelList.get(0).logProb));*/
		return prob;
	}
}
