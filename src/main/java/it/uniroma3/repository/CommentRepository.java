package it.uniroma3.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.model.Comment;


public interface CommentRepository extends CrudRepository<Comment, Long> {
	
}
