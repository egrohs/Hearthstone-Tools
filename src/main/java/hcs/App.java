package hcs;

public class App {

    public static void main(String[] args) {
	CardBuilder.leCards();
	//new SinergyFromText();
	new TagBuilder();
	CardBuilder.generateCardSynergies(CardBuilder.getCard("Leeroy Jenkins"));
    }
}
