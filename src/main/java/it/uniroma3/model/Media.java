package it.uniroma3.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity
public class Media {
	@Id
	private long pk;
	
	@ManyToOne
	private ProfileSubject owner;
	
	@Column(length = 10000)
	private String caption;
	
	private int num_likes;
	
	private int num_comments;
	
	private LocalDateTime timestamp;
	
	@Column(nullable=true)
	private String location;
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="media_pk")
	private List<Comment> comments;
	
	public Media() {}

	public Media(long pk, ProfileSubject owner, String caption, int num_likes, int num_comments, long taken_at,
			String location) {
		super();
		this.pk = pk;
		this.owner = owner;
		this.caption = caption;
		this.num_likes = num_likes;
		this.num_comments = num_comments;
		this.location = location;
		this.SetTimestamp(taken_at);
	}

	public long getPk() {
		return pk;
	}
	
	public void setPk(long pk) {
		this.pk=pk;
	}
	

	public ProfileSubject getOwner() {
		return owner;
	}

	public void setOwner(ProfileSubject owner) {
		this.owner = owner;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public int getNum_likes() {
		return num_likes;
	}

	public void setNum_likes(int num_likes) {
		this.num_likes = num_likes;
	}

	public int getNum_comments() {
		return num_comments;
	}

	public void setNum_comments(int num_comments) {
		this.num_comments = num_comments;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime date) {
		this.timestamp = date;
	}
	
	public void SetTimestamp(long taken_at) {
		this.timestamp=LocalDateTime.ofInstant(Instant.ofEpochSecond(taken_at), ZoneOffset.UTC);  
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	
	
	
	
}
