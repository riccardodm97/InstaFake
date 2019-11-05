package it.uniroma3.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Status {

	@Id
	private String usernameSubject;
	
	private String nextFollower;
	
	private String nextFollowing;
	
	public Status () {}
	
	public Status(String usernameSubject) {
		super();
		this.usernameSubject=usernameSubject;
	}

	public Status(String usernameSubject, String nextFollower, String nextFollowing) {
		super();
		this.usernameSubject = usernameSubject;
		this.nextFollower = nextFollower;
		this.nextFollowing = nextFollowing;
	}

	public String getUsernameSubject() {
		return usernameSubject;
	}

	public void setUsernameSubject(String usernameSubject) {
		this.usernameSubject = usernameSubject;
	}

	public String getNextFollower() {
		return nextFollower;
	}

	public void setNextFollower(String nextFollower) {
		this.nextFollower = nextFollower;
	}

	public String getNextFollowing() {
		return nextFollowing;
	}

	public void setNextFollowing(String nextFollowing) {
		this.nextFollowing = nextFollowing;
	}
	
	
}
