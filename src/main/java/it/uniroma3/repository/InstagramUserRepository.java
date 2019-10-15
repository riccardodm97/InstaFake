package it.uniroma3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.model.InstagramUser;

@Repository
public interface InstagramUserRepository extends CrudRepository<InstagramUser, String> {
	
	public InstagramUser findByPk(Long pk);
	
	public boolean existsByPk(Long pk);
	
}
