package it.uniroma3.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.model.ProfileSubject;
import it.uniroma3.repository.ProfileSubjectRepository;

@Service
public class ProfileSubjectService {
	
	@Autowired
	private ProfileSubjectRepository profSubRepo;
	
	
	@Transactional
	public ProfileSubject inserisci(ProfileSubject p) {
		return this.profSubRepo.save(p);
	}
	
	
	@Transactional
	public ProfileSubject cercaPerUsername(String username) {
		return this.profSubRepo.findById(username).get();
	}
}
