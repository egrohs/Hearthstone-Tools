package hstools;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import hstools.components.CardBuilder;
import hstools.components.DeckBuilder;
import hstools.components.TagBuilder;
import hstools.model.Deck;
import hstools.model.Tag;

@SpringBootApplication//(scanBasePackages = { "hstools.components" })
@ComponentScan("hstools.components")
public class HearthstoneToolsApplication implements CommandLineRunner {
	@Autowired
	private CardBuilder cb;
	
	@Autowired
	private DeckBuilder db;
	
	@Autowired
	private TagBuilder tb;
	
	public static void main(String[] args) {
		SpringApplication.run(HearthstoneToolsApplication.class, args);
	}
	
    @Override
    public void run(String... args) throws Exception {
    	cb.buildCards();
    	
    	ClassLoader cl = this.getClass().getClassLoader();
    	db.loadDecks(new File(cl.getResource("decks").getFile()));
    	
    	tb.importTags();
    	tb.buildCardTags();
    	
    	for (Deck dk : db.getDecks()) {
			dk.calcTags();
			for (Tag t : dk.getFreq().keySet()) {
				System.out.println(t + ": " + dk.getFreq().get(t));
			}
		}
    }
}