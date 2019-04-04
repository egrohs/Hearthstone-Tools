package ht;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ht.model.Card;

/**
 * Leitor de logs do hearthstone durante o jogo.
 * 
 * @author 99689650068
 *
 */
public class ZoneLogReader extends Thread {
    private long pos;
    // private long size;
    // static boolean deleted;
    public static File zonelog = new File("C:\\Program Files (x86)\\Hearthstone\\Logs\\Zone.log");
    static RandomAccessFile zone;
    public static Map<LocalTime, Card> playMap = new TreeMap<LocalTime, Card>();
    // TODO com esse pattern tb esta pegando as cards descartadas
    // indevidamente?
    // static Pattern play = Pattern.compile("OPPOSING (HAND|SECRET) ->");
    static Pattern playCard = Pattern.compile(
	    "D (\\d+\\:\\d+\\:\\d+\\.\\d+) ZoneChangeList\\.ProcessChanges\\(\\) \\- id=(\\d+) local=False \\[name=([\\w\\s]+) id=(\\d+) zone=PLAY zonePos=0 cardId=([\\w\\_]+) player=(\\d)\\] zone from OPPOSING HAND");
    static Pattern hero = Pattern.compile("cardId=([\\w\\s]+) player=(\\d)] to OPPOSING PLAY \\(Hero\\)");
    public static int pendente;
    private static Matcher matcher;
    public static boolean done;

    private void parseFile() {
	if (zonelog.exists() && zone != null) {
	    // System.out.println("ZONELOG EXISTE");
	    // zonelog = new File("C:\\Program Files
	    // (x86)\\Hearthstone\\Logs\\Zone.log");
	    // deleted = false;
	    Card c = null;
	    done = false;
	    String line = null;
	    try {
		while ((line = zone.readLine()) != null) {
		    matcher = hero.matcher(line);
		    if (matcher.find()) {
			//TODO setar o oponente do jogo
			//GameBuilder.opponent.setClasse(CardBuilder.getCard(matcher.group(1)).getClasse());
			System.out.println(CardBuilder.getCard(matcher.group(1)).getClasse());
		    } else {
			matcher = playCard.matcher(line);
			if (matcher.find() || matcher.matches()) {
			    try {
				c = CardBuilder.getCard(matcher.group(5));
				playMap.put(LocalTime.parse(matcher.group(1)), c);
				System.out.println(c);
			    } catch (RuntimeException e) {
				System.err.println(e.getMessage());
			    }
			}
		    }
		}
		pos = zone.getFilePointer();
		zone.close();
		zone = null;
		done = true;
		// System.out.println("ZONE LOG LIDO...");
		Thread.sleep(5000);
	    } catch (NumberFormatException | InterruptedException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else {
	    // tenta ler o log
	    try {
		zone = new RandomAccessFile(zonelog, "r");
		zone.seek(pos);
	    } catch (IOException e) {
		System.err.println("Aguardando nova partida...");
		try {
		    Thread.sleep(10000);
		} catch (InterruptedException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	}
    }

    @Override
    public void run() {
	while (true) {
	    parseFile();
	}
    }
}