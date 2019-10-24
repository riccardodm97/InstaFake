package it.uniroma3.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class InstagramUserDB {

	@Id
	private String username;
	
	@Column(nullable=false,unique=true)
	private long pk;
	
	private String fullName;
	
	private int num_followers;
	
	private int num_following;
	
	private int num_posts;
	
	@Column(length=10000)
	private String bio;
	
	private int num_tags;
	
	@Column(name="anonymous_pic")
	private boolean has_anonymous_profile_pic;
	
	private boolean isVerified;
	
	private boolean isPrivate;
	
	private String location;
	
	private String external_url;
	
	public InstagramUserDB() {}

	public InstagramUserDB(String username, long pk, String fullName, int num_followers, int num_following,
			int num_posts, String bio, boolean isVerified, boolean isPrivate, String location,int tags, boolean anonymous_profile,String url) {
		super();
		this.username = username;
		this.pk = pk;
		this.fullName = fullName;
		this.num_followers = num_followers;
		this.num_following = num_following;
		this.num_posts = num_posts;
		this.bio = bio;
	 	this.isVerified = isVerified;
		this.isPrivate = isPrivate;
		this.location = location;
		this.external_url= url;
		this.num_tags= tags;
		this.has_anonymous_profile_pic= anonymous_profile;
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getPk() {
		return pk;
	}

	public void setPk(long pk) {
		this.pk = pk;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public int getNum_followers() {
		return num_followers;
	}

	public void setNum_followers(int num_followers) {
		this.num_followers = num_followers;
	}

	public int getNum_following() {
		return num_following;
	}

	public void setNum_following(int num_following) {
		this.num_following = num_following;
	}

	public int getNum_posts() {
		return num_posts;
	}

	public void setNum_posts(int num_posts) {
		this.num_posts = num_posts;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public int getNum_tags() {
		return num_tags;
	}

	public void setNum_tags(int num_tags) {
		this.num_tags = num_tags;
	}

	public boolean has_anonymous_profile_pic() {
		return has_anonymous_profile_pic;
	}

	public void setHas_anonymous_profile_pic(boolean has_anonymous_profile_pic) {
		this.has_anonymous_profile_pic = has_anonymous_profile_pic;
	}

	public String getExternal_url() {
		return external_url;
	}

	public void setExternal_url(String external_url) {
		this.external_url = external_url;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
}
