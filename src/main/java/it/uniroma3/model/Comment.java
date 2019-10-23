package it.uniroma3.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Comment {
	
	@Id
	private long pk;
	
	//potrei mettere il riferimento o inserire un media_id che avrebbe la stessa funzione dato che 
	//nel json del commento mi viene passato anche l'id del post a cui si riferisce ( che dovrei inserire a mano)
	private long media_id;

	@ManyToOne
	private InstagramUserDB owner;  //se gia c'è inserisco il collegamento altrimenti devo salvare un nuovo user 
	
	@Column(length=100000)
	private String text;
	
	private float trustworthiness;
	
	private LocalDateTime timestamp;
	
	public Comment() {}

	public Comment(long pk, long media_id, InstagramUserDB owner, String text, Long created_at,float probability) {
		super();
		this.pk = pk;
		this.media_id = media_id;
		this.owner = owner;
		this.text = text;
		this.trustworthiness=probability;
		this.setTimestamp(created_at);
	}

	public long getPk() {
		return pk;
	}

	public void setPk(long pk) {
		this.pk = pk;
	}

	public long getMedia_id() {
		return media_id;
	}

	public void setMedia_id(long media_id) {
		this.media_id = media_id;
	}

	public InstagramUserDB getOwner() {
		return owner;
	}

	public void setOwner(InstagramUserDB owner) {
		this.owner = owner;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	public float getTrustworthiness() {
		return trustworthiness;
	}

	public void setTrustworthiness(float trustworthiness) {
		this.trustworthiness = trustworthiness;
	}

	public LocalDateTime getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(LocalDateTime date) {
		this.timestamp = date;
	}
	
	public void setTimestamp(Long created_at) {
		this.timestamp=LocalDateTime.ofInstant(Instant.ofEpochSecond(created_at), ZoneOffset.UTC);  
	}
	
	
}
