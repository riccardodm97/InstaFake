package it.uniroma3.analytics;

import java.util.List;

import org.springframework.stereotype.Component;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

//utilizzo la libreria emoji-java

@Component
public class TextPreprocessing {
	
	public String Process(String text) {
		String processed_text=null;
		processed_text=this.substituteEmoji(text);
		
		return processed_text;
	}
	
	
	//sostituisce ogni emoji nel testo con la scritta [emoticon]
	public String substituteEmoji(String text) {
		String processed_text=null;
		processed_text=EmojiParser.replaceAllEmojis(text, "[emoticon]");
		return processed_text;
	}

	//se il testo contiene emoji le elimina e sostituisce la stringa [emoticon] alla fine del testo tante volte quante sono le emoji
	public String substituteEmojiCustom(String text) {
		int num_emoji=0; 
		String processed_text=null;
		if(EmojiManager.containsEmoji(text)) {
			List<String> emojis=EmojiParser.extractEmojis(text);    //prendo tutte le emoji contenute nel testo per contarle
			num_emoji=emojis.size();								//conto il numero di emoticon nel testo
			processed_text=EmojiParser.removeAllEmojis(text);       //rimuovo le emoji dal testo
		}
		else processed_text=text;

		//inserisco la scritta [emoticon] nel testo tante volte quante erano le emoji in quello originale
		for(int i=0;i<num_emoji;i++) {
			processed_text.concat(" [emoticon]");
		}

		return processed_text;
	}
	
	//implementare altri metodi di processamento del testo?? rimozione punteggiatura, rimozione parole non chiave ??
}
