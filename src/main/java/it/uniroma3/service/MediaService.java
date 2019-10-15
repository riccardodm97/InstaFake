package it.uniroma3.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.model.Media;
import it.uniroma3.repository.MediaRepository;

@Service
public class MediaService {
	
	@Autowired
	private MediaRepository mediaRepo;
	
	@Transactional
	public Media inserisci(Media m) {
		return this.mediaRepo.save(m);
	}
}
