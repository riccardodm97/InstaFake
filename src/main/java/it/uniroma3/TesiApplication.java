package it.uniroma3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.uniroma3.logic.AppLogic;


@SpringBootApplication
public class TesiApplication implements CommandLineRunner{
	
	@Autowired
	private AppLogic logic;
	
	public static void main(String[] args) {
		SpringApplication.run(TesiApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
       this.logic.Start();
    }

}
