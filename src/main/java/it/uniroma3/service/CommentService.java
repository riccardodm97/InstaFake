package it.uniroma3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.model.Comment;
import it.uniroma3.repository.CommentRepository;

@Service
public class CommentService {
	
	@Autowired
	private CommentRepository commentRepo;
	
	public Comment inserisci(Comment c) {
		return this.commentRepo.save(c);
	}
	
	
}
