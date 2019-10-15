package it.uniroma3.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class ProfileSubject {
	
	@Id
	private String username;
	
	@OneToOne
	private InstagramUser profile;
	
	@ManyToMany
	@JoinTable(name="followers", joinColumns={@JoinColumn(name="subject")}, 
		       inverseJoinColumns={@JoinColumn(name="follower")})
	private List<InstagramUser> followers;
	
	@ManyToMany
	@JoinTable(name="following", joinColumns={@JoinColumn(name="subject")}, 
	           inverseJoinColumns={@JoinColumn(name="following")})
	private List<InstagramUser> following;
	
	@OneToMany(mappedBy="owner")
	private List<Media> posts;
	
	public ProfileSubject() {}

	public ProfileSubject(String username) {
		this.username=username;
	}
	
	public ProfileSubject(String username, InstagramUser profile, List<InstagramUser> followers, List<InstagramUser> following,
			List<Media> posts) {
		super();
		this.username = username;
		this.profile=profile;
		this.followers = followers;
		this.following = following;
		this.posts = posts;
	}

	public InstagramUser getProfile() {
		return profile;
	}

	public void setProfile(InstagramUser profile) {
		this.profile = profile;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<InstagramUser> getFollowers() {
		return followers;
	}

	public void setFollowers(List<InstagramUser> followers) {
		this.followers = followers;
	}

	public List<InstagramUser> getFollowing() {
		return following;
	}

	public void setFollowing(List<InstagramUser> following) {
		this.following = following;
	}

	public List<Media> getPosts() {
		return posts;
	}

	public void setPosts(List<Media> posts) {
		this.posts = posts;
	}
	
	
	
	
}
