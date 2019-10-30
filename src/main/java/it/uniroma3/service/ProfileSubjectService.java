package it.uniroma3.service;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.model.InstagramUserDB;
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
		ProfileSubject ps=this.profSubRepo.findById(username).get();
		Hibernate.initialize(ps.getFollowers());
		Hibernate.initialize(ps.getFollowing());
		return ps;
	}
	
	
	
}
