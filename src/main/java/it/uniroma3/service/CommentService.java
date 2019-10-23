package it.uniroma3.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.model.Comment;
import it.uniroma3.repository.CommentRepository;

@Service
public class CommentService {
	
	@Autowired
	private CommentRepository commentRepo;
	
	@Transactional
	public Comment inserisci(Comment c) {
		return this.commentRepo.save(c);
	}
	
	@Transactional
	public List<Comment> getAllComments(){
		return (List<Comment>) this.commentRepo.findAll();
	}
	
	
}
