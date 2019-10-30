package it.uniroma3.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.model.Media;

@Repository
public interface MediaRepository extends CrudRepository<Media,Long>{
	
	public long countByOwner_username(String username);
	
	public List<Media> findAllByOwner_username(String username);
	
}
