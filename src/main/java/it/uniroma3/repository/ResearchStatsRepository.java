package it.uniroma3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.model.ResearchStats;

@Repository
public interface ResearchStatsRepository extends CrudRepository<ResearchStats,String> {

	
	
}
