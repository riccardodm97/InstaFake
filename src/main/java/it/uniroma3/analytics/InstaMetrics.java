package it.uniroma3.analytics;

public final class InstaMetrics {

	private final static int MAX_FOLLOWING=7500;   //numero massimo di account che instagram permette di seguire
	
	private final static int MAX_STAT_GENERIC_FOLLOWING=0; //upperbound al numero di following per account generico basato su dati statistici
	
	private final static int SUSPECT_STAT_GENERIC_FOLLOWING=0;   //un numero di following superiore è sospetto per account medio
	
	private final static int MAX_STAT_POPULAR_FOLLOWING=0;     //upperbound al numero di following per account popolare(da 15k+) basato su dati statistici
	
	private final static int SUSPECT_STAT_POPULAR_FOLLOWING=0;   //un numero di following superiore è sospetto per account popolare
	
	
	//follower to followinf ratio tra i 1000
	
	private final static float FFR_0=1F;
	
	
	//follower to following ffr ratio tra i 1000 e i 15k follower
	
	                                            //molto sospetto  <0.5
	private final static float FFR_1=0.5F;     
	                                            //sospetto  tra 0.5 e 1
	private final static float FFR_2=1F;       
	                                            //normale  tra 1 e 2
	private final static float FFR_3=2F;       
	                                            //microinfluencer  tra 2 a 10+
	private final static float FFR_4=10F;      
	
	
	//like to follower ratio lfr (numero di like medio per numero di follower)
	
	private final static float LFR_TO5K=0.10F;               //fino a 5k follower
	
	private final static float LFR_TO10K=0.058F;             //da 5k a 10k follower
	
	private final static float LFR_TO25K=0.037F;             //da 10k a 25k follower
	
	private final static float LFR_TO50K=0.03F;              //da 25k a 50k follower
	
	private final static float LFR_TO500K=0.027F;            //da 50k a 500k follower
	
	private final static float LFR_TOMAX=0.02F;              //oltre 500k follower
	
	
	private final static float SUSPECT_LFR_DEVIATION=0.15F;     //scostamento(negativo) di lfr rispetto allo standard che risulta sospetto
	
	private final static float SUSPETCT_LIKE_DEVIATION=0.4F;    //scostamento(positivo) di like rispetto alla media dei post che risulta sospetto
	
	
	//engagement rate (er) ((num tot like +num tot commenti)/tot post)/followers
	
	                                               //basso er <1%
	private final static float ER_1=0.01F;         
	                                               //medio-buono er tra 1 e 3.5%
	private final static float ER_2=0.035F;       
	                                               //alto er  tra 3.5 3 6%
	private final static float ER_3=0.06F;        
	                                               //ottimo er  oltre 6%
	
	//troppo basso non influenza troppo alto con troppi pochi follower falsificato
	
	//hashtag
	
	private final static int MAX_NUM_HASHTAG=3;        //numero medio di hashtag per post di utente medio 
	
	//like to comments rate per account con almeno 15k follower
	
	private final static float MAX_LTC=0.01F;         //massimo di ltc realistica
	
	
	
	
	
	
	           
	
}
