package it.uniroma3.analytics;

public final class InstaMetrics {

	private final static int MAX_FOLLOWING=7500;   //numero massimo di account che instagram permette di seguire
	
	private final static int MAX_STAT_GENERIC_FOLLOWING=0; //upperbound al numero di following per account generico basato su dati statistici
	
	private final static int SUSPECT_STAT_GENERIC_FOLLOWING=0;   //un numero di following superiore è sospetto per account medio
	
	private final static int MAX_STAT_POPULAR_FOLLOWING=0;     //upperbound al numero di following per account popolare(da 15k+) basato su dati statistici
	
	private final static int SUSPECT_STAT_POPULAR_FOLLOWING=0;   //un numero di following superiore è sospetto per account popolare
	
	
	//follower to followinf ratio tra i 1000
	
	private final static double MINFFR_0=0;
	
	private final static double MAXFFR_0=0;
	
	
	//follower to following ffr ratio tra i 1000 e i 15k follower
	
	                                            //molto sospetto  <0.5
	private final static double FFR_1=0.5;     
	                                            //sospetto  tra 0.5 e 1
	private final static double FFR_2=1;       
	                                            //normale  tra 1 e 2
	private final static double FFR_3=2;       
	                                            //microinfluencer  tra 2 a 10+
	private final static double FFR_4=10;      
	
	
	//like to follower ratio lfr (numero di like medio per numero di follower)
	
	private final static double LFR_TO5K=0.10;               //fino a 5k follower
	
	private final static double LFR_TO10K=0.058;             //da 5k a 10k follower
	
	private final static double LFR_TO25K=0.037;             //da 10k a 25k follower
	
	private final static double LFR_TO50K=0.03;              //da 25k a 50k follower
	
	private final static double LFR_TO500K=0.027;            //da 50k a 500k follower
	
	private final static double LFR_TOMAX=0.02;              //oltre 500k follower
	
	
	private final static double SUSPECT_LFR_DEVIATION=0.4;     //scostamento(negativo) di lfr rispetto allo standard che risulta sospetto
	
	private final static double SUSPETCT_LIKE_DEVIATION=0.4;    //scostamento(positivo) di like rispetto alla media dei post che risulta sospetto
	
	
	//engagement rate (er) ((num tot like +num tot commenti)/tot post)/followers
	
	                                               //basso er <1%
	private final static double ER_1=0.01;         
	                                               //medio-buono er tra 1 e 3.5%
	private final static double ER_2=0.035;       
	                                               //alto er  tra 3.5 3 6%
	private final static double ER_3=0.06;        
	                                               //ottimo er  oltre 6%
	
	
	//comment to follower ratio
	
	private final static double CFR_TO1K=0.0056;
	
	private final static double CFR_TO10K=0.0027;

	private final static double CFR_TO100K=0.0014;
	
	private final static double CFR_TO1M=0.0006;
	
	
	//hashtag
	
	private final static int MAX_NUM_HASHTAG=3;        //numero medio di hashtag per post di utente medio 
	
	//like to comments rate per account con almeno 15k follower
	
	private final static double MAX_LTC=0.01;         //massimo di ltc realistica esatto!! ragazzo
	
	
	public double LFR_an(double lfr,int follower_count) {
		
		double deviation;
		
		if(follower_count<=1000) {
			deviation=this.CFR_deviation(lfr, InstaMetrics.CFR_TO1K);
		}else if(follower_count<=10000) {
			deviation=this.CFR_deviation(lfr, InstaMetrics.CFR_TO10K);
		}else if(follower_count<=100000) {
			deviation=this.CFR_deviation(lfr, InstaMetrics.CFR_TO100K);
		}else {
			deviation=this.CFR_deviation(lfr, InstaMetrics.CFR_TO1M);
		}
		
		return deviation;
	}
	
	public double LFR_deviation(double actual_cfr,double expected_cfr) {
		
		return expected_cfr-actual_cfr;
	}
	
	
public double CFR_an(double lfr,int follower_count) {
		
		double deviation;
		
		if(follower_count<=5000) {
			deviation=this.LFR_deviation(lfr, InstaMetrics.LFR_TO5K);
		}else if(follower_count<=10000) {
			deviation=this.LFR_deviation(lfr, InstaMetrics.LFR_TO10K);
		}else if(follower_count<=25000) {
			deviation=this.LFR_deviation(lfr, InstaMetrics.LFR_TO25K);
		}else if(follower_count<=50000) {
			deviation=this.LFR_deviation(lfr, InstaMetrics.LFR_TO50K);
		}else if(follower_count<=500000){
			deviation=this.LFR_deviation(lfr, InstaMetrics.LFR_TO500K);
		}else {
			deviation=this.LFR_deviation(lfr, InstaMetrics.LFR_TOMAX);
		}
		
		return deviation;
	}
	
	public double CFR_deviation(double actual_lfr,double expected_lfr) {
		
		return expected_lfr-actual_lfr;
	}
	
	
	
	
	
	           
	
}
