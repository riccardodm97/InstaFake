package it.uniroma3.analytics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
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
import it.uniroma3.service.StatusService;

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

	@Autowired 
	private StatusService statusService;

	private JFastText jft;           //libreria per il machine learning sui commenti

	private ResearchStats rs;        //classe per i risultati dell'analisi

	private ProfileSubject ps;       //profilo su cui si è basata la ricerca
	
	private FileWriter commentWriter;  //per scrivere i commenti con la loro classificazione su file di testo

	private DecimalFormat f= new DecimalFormat("0.00");   //per arrotondare i risultati alla seconda cifra decimale
	
	public void StartDataAnalysis(String user) {

		if(!this.profileService.esiste(user)) {
			System.out.println("\n[non esiste ancora una ricerca su questo username, prima creala]\n");
			return ;
		}

		if(!statusService.cercaPerUsernameSubject(user).getNextFollower().equals("finito") ||
				!statusService.cercaPerUsernameSubject(user).getNextFollowing().equals("finito")) {
			System.out.println("\n[la ricerca su questo user non è finita, alcuni dati potrebbero mancare, terminala prima di procedere]\n");
			return ;

		}

		//ricerco il profilo di cui ho estratto i dati 
		this.ps=this.profileService.cercaPerUsername(user);

		//inizializzo la classe dei risultati
		this.rs=new ResearchStats(user);

		//analizzo i following del profilo
		try {
			
			this.FollowingAnalysis();
		
		}catch (IOException e1) {
			System.out.println("[error creating new file (following_data)]\n");
			e1.printStackTrace();
		}

		//analizzo i follower del profilo
		try {
			
			this.FollowersAnalysis();
		
		}catch (IOException e) {
			System.out.println("[error creating new file (followers_data)]\n");
			e.printStackTrace();
		}

		//inizializzo la classe della libreria da usare per la classification del testo
		this.jft=new JFastText();    

		//analizzo i post estratti e i relativi commenti
		try {
			
			this.StudyMediaData();
			
		} catch (IOException e) {
			System.out.println("[error creating new file (comments_data)]\n");
			e.printStackTrace();
		}

		//conto il numero di account privati con meno di 1k followers che l'utente segue
		this.less1kprivateFollowing();

		//calcolo ffr , lfr , cfr
		this.lfr_calc();
		this.ffr_calc();
		this.cfr_calc();

		//salvo nel db i dati analizzati
		this.researchService.inserisci(rs);
	}

	//per determinare numero medio di : like , commenti e hashtag 
	public void StudyMediaData() throws IOException {

		List<Media> posts=this.mediaService.getAllMediaByOwnerUser(this.rs.getUsername());
		
		//apro il file di testo su cui scrivere i commenti con la loro valutazione
		this.commentWriter= createFile("comments_data");
	
		int num_comm=0;                     //tot comments
		int num_comm_estratti=0;            //commments fetched from insta
		int num_likes=0;
		int num_hashtag=0;
		int num_tot_fake=0;
		int num_posts=posts.size();

		for(Media m: posts) {
			
			List<Comment> commenti=m.getComments();

			num_likes+=m.getNum_likes();

			num_comm+=m.getNum_comments();

			num_hashtag+=this.extractAndCountHashtag(m.getCaption());

			if(commenti!=null) {

				num_comm_estratti+=commenti.size();

				num_tot_fake+=this.AnalyzeComments(commenti);               //analizzo i commenti estratti per post     
			}
			commenti.clear();
		}
		
		this.commentWriter.close();    //chiudo il writer dopo aver analizzato e scritto tutti i commenti        

		this.rs.setPost_count(num_posts);

		this.rs.setAvgComments_count((double)num_comm/num_posts);

		this.rs.setAvgLike_count((double)num_likes/num_posts);

		this.rs.setAvgHashtag_count((double)num_hashtag/num_posts);

		if(num_comm>0) this.commentsDataProcess(num_tot_fake,num_comm_estratti);

		this.engagementRate_calc(num_likes, num_comm, num_posts);
	}


	public void commentsDataProcess(int num_fake,int num_comm) {

		this.rs.setFakeComments_count(num_fake);

		this.rs.setAvgFakeComments_count(num_fake/(double) this.rs.getPost_count());                     //se non ho preso tutti i commenti è più bassa (almeno questo numero)

		this.rs.setFaketotComment_ratio(num_fake/(double)num_comm);

		this.rs.setAvgLtc_ratio(rs.getAvgLike_count()/rs.getAvgComments_count());                        //like to comment ratio
	}


	//conto il numero di hashtag per caption di un post
	public int extractAndCountHashtag(String text) {

		return StringUtils.countOccurrencesOf(text, "#");
	}

	//followers to following ratio calculation
	public void ffr_calc() {
		double ratio=0;

		int followers_count=this.ps.getProfile().getNum_followers();

		int following_count=this.ps.getProfile().getNum_following();

		if(followers_count==0) followers_count=1;

		ratio=followers_count/(double)following_count;

		this.rs.setFfr(ratio);	

		this.rs.setFollowers_count(followers_count);

		this.rs.setFollowing_count(following_count);
	}


	//like to followers ratio calculation
	public void lfr_calc() {

		int followers_count=this.ps.getProfile().getNum_followers();

		double ratio=this.rs.getAvgLike_count()/followers_count;

		this.rs.setLfr(ratio);
	}

	//comment to followers ratio calculation
	public void cfr_calc(){

		int followers_count= this.ps.getProfile().getNum_followers();

		double ratio=this.rs.getAvgComments_count()/followers_count;

		this.rs.setCfr(ratio);
	}


	//engagement rate calculation
	public void engagementRate_calc(int tot_likes_count,int tot_comm_count,int post_count) {

		double er=0;

		int followers_count=this.ps.getProfile().getNum_followers();

		if(followers_count==0) followers_count=1;

		er=((tot_comm_count+tot_likes_count)/post_count)/(double)followers_count;

		this.rs.setEngagement_rate(er);
	}


	public void FollowingAnalysis() throws IOException {

		FileWriter writer= createFile("following_data");
		
		int fake_following=0;

		List<InstagramUserDB> following=this.ps.getFollowing();

		for(InstagramUserDB user: following) {

			double value=this.AnalyzeUser(user);
			
			value=Double.parseDouble(f.format(value).replace(",", "."));             // per il problema di arrotondamento con i double

			if(value>=0.60) {                                                      //soglia del 60 % di suspect
				fake_following+=1;
			}
			
			
			writer.write(user.getUsername()+" con "+ value +"\n");                        //scrivo sul file l'username e il valore di suspect

			value=0;
		}
		
		writer.close();  //chiudo il file

		this.rs.setSuspect_following_count(fake_following);
	}

	public void FollowersAnalysis() throws IOException {

		FileWriter writer= createFile("followers_data");

		int fake_followers=0;

		List<InstagramUserDB> followers=this.ps.getFollowers();

		for(InstagramUserDB user: followers) {

			double value=this.AnalyzeUser(user);
			
			value=Double.parseDouble(f.format(value).replace(",", "."));             // per il problema di arrotondamento con i double
			
			if(value>=0.80) {                                                       
				fake_followers+=1;                                                 
			}

			
			writer.write(user.getUsername()+" con "+ value +"\n");                        //scrivo sul file l'username e il valore di suspect
			
			value=0;                                                               
		}
		
		writer.close();  //chiudo il file
		
		this.rs.setSuspect_followers_count(fake_followers);
	}
	
	public FileWriter createFile(String name) throws IOException {
		
		File followers_data= new File("src/main/resources/data/"+name+".txt");
		
		followers_data.createNewFile();
		
		FileWriter writer= new FileWriter(followers_data);
		
		return writer;
	}


	public double AnalyzeUser(InstagramUserDB user) {
		
		double value=0.0;

		if(user.isVerified()) return 0;    //se l'account è verificato lo considero comunque genuino

		if(user.getFullName().equals("fail")) return 0;     //se non ho potuto ottenere i dati non lo considero 

		if(user.getNum_following()!=0) {

			double ratio=user.getNum_followers()/(double) user.getNum_following();

			if(user.getNum_followers()<=7500) {       //condizione sotto i 7500 perchè sopra può solo aumentare
				
			  //if (ratio<=1 && ratio>0.5)    value+=0.05;       //inserire questa condizione 
				if (ratio<=0.5 && ratio>0.2) value+=0.1; 
				if (ratio<=0.2) value+=0.2;                        

			}
			else {
				if (ratio<=1.1) value+=0.2; 
			}
		}	

		if(!user.isPrivate() && user.getNum_followers()<=15) value+=0.1;
		if(!user.isPrivate() &&  user.getNum_followers()<=10) value+=0.2;

		if(user.getNum_following()>=1500 && user.getNum_following()<3000) value+=0.1;
		if(user.getNum_following()>=3000 && user.getNum_following()<5000) value+=0.2;
		if(user.getNum_following()>=5000) value+=0.3;

		if(user.getNum_posts()<=5 && !user.has_anonymous_profile_pic()) value+=0.1;
		if(user.getNum_posts()<=5 && user.has_anonymous_profile_pic()) value+=0.2;

		if(user.has_anonymous_profile_pic()) value+=0.3;

		if(!StringUtils.hasText(user.getBio())) value+=0.2;

		if(!user.isPrivate() && user.getNum_tags()<=3) value+=0.1;

		if(this.alphabeticRatio(user.getUsername())<=0.4) value+=0.1;

		return value;
	}


	//return the number of letters to the number of other characters in username
	public float alphabeticRatio(String username) {

		float ratio;
		int count=0;
		
		for (int i=0; i<username.length(); i++) {
			if (Character.isLetter(username.charAt(i))) {
				count++;  
			}
		}

		ratio=count/(float) username.length();

		return ratio;
	}


	//numero di account che il soggetto segue , con meno di 1000 followers e profilo privato 

	public void less1kprivateFollowing() {

		int numero=0;

		for(InstagramUserDB user:this.ps.getFollowing()) {
			if(user.getNum_followers()<=1000 && user.isPrivate()) numero++;
		}

		this.rs.setLess1kfandprivate(numero);
	}


	//determino quanti commenti fake ci sono 

	public int AnalyzeComments(List<Comment> commenti) throws IOException {


		int num_fake_per_post=0;        //numero di commenti ritenuti falsi (sopra il 70% prob)

		this.jft.loadModel("src/main/resources/models/model.bin");    //carico il modello trainato

		for(Comment c:commenti) {


			double prob=this.TextClassification(c.getText());     //per ogni commento setto il livello di attendibilità 

			c.setFalse_prob(prob);

			this.commentService.inserisci(c);

			if(prob>=0.80) num_fake_per_post+=1;                   //soglia del 80% di suspect

		}

		return num_fake_per_post;
	}

	public double TextClassification(String text) throws IOException {
		
		double prob;

		//preprocessing del testo
		String processed_text= txtProcess.Process(text); 

		List<JFastText.ProbLabel> probLabelList = jft.predictProba(processed_text,2);     //predizione della label 

		//inserisco il valore della label false
		if(probLabelList.get(0).label.equals("__label__false")) prob=Math.exp(probLabelList.get(0).logProb);
		else prob=Math.exp(probLabelList.get(1).logProb);

		//debug
		String newText= processed_text.replace("\n", "");                  
		this.commentWriter.write("\nThe label of '"+newText+"' is '"+probLabelList.get(0).label+"' with probability "+Math.exp(probLabelList.get(0).logProb));
		
		return prob;
	}
}
