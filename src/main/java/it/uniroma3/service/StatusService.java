package it.uniroma3.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.model.Status;
import it.uniroma3.repository.StatusRepository;

@Service
public class StatusService {
	
	@Autowired
	private StatusRepository statusRepo;
	
	@Transactional
	public Status inserisci(Status s) {
		return this.statusRepo.save(s);
	}
	
	@Transactional
	public Status cercaPerUsernameSubject(String username) {
		return this.statusRepo.findById(username).get();
	}
}
