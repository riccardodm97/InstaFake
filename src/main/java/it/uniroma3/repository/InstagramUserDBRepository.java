package it.uniroma3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.model.InstagramUserDB;

@Repository
public interface InstagramUserDBRepository extends CrudRepository<InstagramUserDB, String> {
	
	public InstagramUserDB findByPk(Long pk);
	
	public boolean existsByPk(Long pk);
	
}
