package it.uniroma3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.model.Status;

@Repository
public interface StatusRepository extends CrudRepository<Status,String>{
	
}
