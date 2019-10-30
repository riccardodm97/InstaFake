package it.uniroma3.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.model.ResearchStats;
import it.uniroma3.repository.ResearchStatsRepository;

@Service
public class ResearchStatsService {

	@Autowired 
	private ResearchStatsRepository researchrepo;
	
	@Transactional
	public ResearchStats inserisci(ResearchStats rs) {
		return this.researchrepo.save(rs);
	}
	
}
