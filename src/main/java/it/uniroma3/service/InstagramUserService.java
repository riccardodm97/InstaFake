package it.uniroma3.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.model.InstagramUserDB;
import it.uniroma3.repository.InstagramUserRepository;

@Service
public class InstagramUserService {
	
	@Autowired
	private InstagramUserRepository instaURepo;;
	
	@Transactional
	public InstagramUserDB inserisci(InstagramUserDB u) {
		return this.instaURepo.save(u);
	}
	
	@Transactional
	public InstagramUserDB trovaPerUsername(String username) {
		return this.instaURepo.findById(username).get();
	}
	
	@Transactional
	public InstagramUserDB trovaPerPk(Long pk) {
		return this.instaURepo.findByPk(pk);
	}
	
	@Transactional
	public boolean esiste(String username) {
		return this.instaURepo.existsById(username);
	}

}
