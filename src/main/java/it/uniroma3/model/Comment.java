package it.uniroma3.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Comment {
	
	@Id
	private long pk;

	/*@ManyToOne
	private InstagramUserDB owner;*/  //proprietario del commento
	
	@Column(length=100000)
	private String text;
	
	private double false_prob;
	
	private LocalDateTime timestamp;
	
	public Comment() {}

	public Comment(long pk, InstagramUserDB owner, String text, Long created_at) {
		super();
		this.pk = pk;
		//this.owner = owner;
		this.text = text;
		this.setTimestamp(created_at);
	}

	public long getPk() {
		return pk;
	}

	public void setPk(long pk) {
		this.pk = pk;
	}

	/*public InstagramUserDB getOwner() {
		return owner;
	}

	public void setOwner(InstagramUserDB owner) {
		this.owner = owner;
	}*/

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getFalse_prob() {
		return false_prob;
	}

	public void setFalse_prob(double false_prob) {
		this.false_prob = false_prob;
	}

	public LocalDateTime getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(LocalDateTime date) {
		this.timestamp = date;
	}
	
	public void setTimestamp(long created_at) {
		this.timestamp=LocalDateTime.ofInstant(Instant.ofEpochSecond(created_at), ZoneOffset.UTC);  
	}
	
	
}
