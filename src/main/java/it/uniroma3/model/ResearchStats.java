package it.uniroma3.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

//classe per la visualizzazione dei risultati dell'analisi sul profilo 

@Entity
public class ResearchStats {

	@Id
	private String username;
	
	private int post_count;                              //number of user's post retrived form instagram 
	
	@Column(name="avgComm")
	private double avgComments_count;                    //average comments per post
	
	@Column(name="avgLike")
	private double avgLike_count;                        //average like per post
	
	@Column(name="avgHashtag")
	private double avgHashtag_count;                     //average hashtag per post
	
	@Column(name="fakeComments")
	private int fakeComments_count;                      //number of fake comments spotted on post retrived
	
	private double faketotComment_ratio;                //number of fake comments on number of total comments
	
	@Column(name="avgFakeComments")
	private double avgFakeComments_count;                //average fake comments per post
	
	private int suspect_followers_count;                 //number of followers that are suspect 
	
	private int suspect_following_count;                  //number of followed account that are suspect
	
	@Column(name="er")
	private double engagement_rate;                      //engagement rate 
	
	@Column(name="less1kf")
	private int follow_less1kf;                          //number of followed account with less than 1k followers and private account
	
	@Column(name="avgLtc")
	private double avgLtc_ratio;                         //average like to comment ratio
	
	private double ffr;                                  //follower to following ratio
	
	private double lfr;                                  //like to follower ratio
	
	
	
	public ResearchStats() {}

	
	public ResearchStats(String user) {
		super();
		this.username=user;
	}


	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public int getPost_count() {
		return post_count;
	}


	public void setPost_count(int post_count) {
		this.post_count = post_count;
	}


	public double getAvgComments_count() {
		return avgComments_count;
	}



	public void setAvgComments_count(double avgComments_count) {
		this.avgComments_count = avgComments_count;
	}



	public double getAvgLike_count() {
		return avgLike_count;
	}



	public void setAvgLike_count(double avgLike_count) {
		this.avgLike_count = avgLike_count;
	}



	public double getAvgLtc_ratio() {
		return avgLtc_ratio;
	}



	public void setAvgLtc_ratio(double avgLtc_ratio) {
		this.avgLtc_ratio = avgLtc_ratio;
	}



	public double getFfr() {
		return ffr;
	}



	public void setFfr(double ffr) {
		this.ffr = ffr;
	}



	public double getLfr() {
		return lfr;
	}



	public void setLfr(double lfr) {
		this.lfr = lfr;
	}



	public int getFollow_less1kf() {
		return follow_less1kf;
	}



	public void setFollow_less1kf(int follow_less1kf) {
		this.follow_less1kf = follow_less1kf;
	}



	public double getFaketotComment_ratio() {
		return faketotComment_ratio;
	}


	public void setFaketotComment_ratio(double faketotComment_ratio) {
		this.faketotComment_ratio = faketotComment_ratio;
	}


	public int getFakeComments_count() {
		return fakeComments_count;
	}



	public void setFakeComments_count(int fakeComments_count) {
		this.fakeComments_count = fakeComments_count;
	}



	public double getAvgFakeComments_count() {
		return avgFakeComments_count;
	}



	public void setAvgFakeComments_count(double avgFakeComments_count) {
		this.avgFakeComments_count = avgFakeComments_count;
	}



	public double getEngagement_rate() {
		return engagement_rate;
	}



	public void setEngagement_rate(double engagement_rate) {
		this.engagement_rate = engagement_rate;
	}



	public int getSuspect_followers_count() {
		return suspect_followers_count;
	}



	public void setSuspect_followers_count(int suspect_followers_count) {
		this.suspect_followers_count = suspect_followers_count;
	}



	public int getSuspect_following_count() {
		return suspect_following_count;
	}



	public void setSuspect_following_count(int suspect_folowing_count) {
		this.suspect_following_count = suspect_folowing_count;
	}



	public double getAvgHashtag_count() {
		return avgHashtag_count;
	}



	public void setAvgHashtag_count(double avgHashtag_count) {
		this.avgHashtag_count = avgHashtag_count;
	}
	
	
	
	
	
	
	
}
