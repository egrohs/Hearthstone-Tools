package hstools.domain.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import hstools.Constants.Archtype;
import hstools.Constants.Format;
import hstools.domain.entities.Card;
import hstools.domain.entities.CardStats;
import hstools.domain.entities.Deck;
import hstools.domain.entities.Node;
import hstools.domain.entities.Tag;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Load/unload downloaded decks from local files.
 * 
 * @author egrohs
 *
 */
@Slf4j
@Service
@DependsOn(value = { "Cards" })
public class DeckComponent {
	@Autowired
	private CardComponent cardComp;

	@Getter
	private Set<Deck> decks = new LinkedHashSet<>();

	public List<Card> calcSuggestions(Deck d) {
		Map<Tag, Integer> deckTags = new HashMap<>();// d.getStats().getSuggests();// TODO ordenado por freq?
		for (Card c : d.getCards().keySet()) {
			for (Tag t : c.getTags()) {
				if (deckTags.get(t) == null) {
					deckTags.put(t, 1);
				} else {
					deckTags.put(t, deckTags.get(t) + 1);
				}
			}
		}
		List<Card> possibles = cardComp.getCards().stream()
				.filter(c -> c.getClasses().contains(d.getClasse()) || c.getClasses().contains("Neutral"))
				.collect(Collectors.toList());
		// d.getCards().keySet().stream().forEach(c -> possibles.remove(c));
		for (Card card : possibles) {
			card.getStats().setTempDeckFreq(0);
			for (Tag t : deckTags.keySet()) {
				if (card.getTags().contains(t)) {
					card.getStats().setTempDeckFreq(card.getStats().getTempDeckFreq() + deckTags.get(t));
				}
			}
		}

		Map<Tag, Integer> topTen = deckTags.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		for (Tag t : topTen.keySet()) {
			System.out.println(t.getNome() + " - " + topTen.get(t));
		}

		// d.getStats().setSuggests(deckTags);
		Collections.sort(possibles,
				Comparator.comparing(Card::getStats, Comparator.comparing(CardStats::getTempDeckFreq)).reversed());
		// c -> c.getStats().getTempDeckFreq()).reversed());
		return possibles.subList(0, 20);
	}

	public void calcStats(Deck deck) {
		if (deck != null) {
			// Build deck tags
			for (Card c : deck.getCards().keySet()) {
				List<String> tags = c.getTags().stream().map(Node::getNome).collect(Collectors.toList());
				deck.getStats().incStats_cost(c.getStats().getStats_cost());
				if ("MINION".equalsIgnoreCase(c.getType())) {
					if (c.getCost() < 3) {
						deck.getStats().incLow_cost_minions(1);
					} else if (c.getCost() < 6 || tags.stream().anyMatch(List.of("COST_MODIFY")::contains)) {// TODO nao
																												// someente
																												// minions
						deck.getStats().incMed_cost_minions(1);
					}
				}
				if (c.getCost() != null && c.getCost() > 5) {
					deck.getStats().incHigh_cost(1);
				}
//			for (Tag t : c.getTags()) {
//				// System.out.print("caard " + c+" ");
//				deck.acumTagSynergy(t, s.getFreq());
//			}
//			if (tags.stream().anyMatch(List.of("DD", "")::contains)) {
//				// DD
//				deck.getStats().incDd(s.getFreq());
//			}
				Integer freq = deck.getCards().get(c);
				if (tags.stream().anyMatch(List.of("DRAW", "GENERATE")::contains)) {
					// CARD_ADV
					deck.getStats().incCard_adv(freq);
				}
				if (tags.stream()
						.anyMatch(List.of("REMOVALS", "DAMAGE_ALL", "DAMAGE_ENEMIES", "DEAL_DAMAGE")::contains)) {
					// BOARD CONTROL
					deck.getStats().incBoard_control(freq);
				}
				if (tags.stream().anyMatch(List.of("TAUNT", "LIFESTEAL", "ARMOR", "HEALTH_RESTORE")::contains)) {
					// SURVIVABILITY
					deck.getStats().incSurv(freq);
				}
			}
			// TODO FINISHER, spells
			// System.out.println(deck.getStats());
		}
	}

	public void unloadDeck(String deckString) {
		// TODO Auto-generated method stub

	}

	public void decodeDecksFromFile(String fname) {
		log.debug("decodificando decks...");
		long ini = System.currentTimeMillis();
		// Scanner sc = null;
		try (Scanner sc = new Scanner(new File("src/main/resources/decks/deckStrings/" + fname))) {
			while (sc.hasNextLine()) {
				String[] line = sc.nextLine().split("\t");
				// System.out.println(deckstr);
				Deck deck = decodeDeckString(line[0]);
				if (line.length > 1)
					deck.getStats().setArchtype(Archtype.valueOf(line[1]));
				if (deck != null)
					decks.add(deck);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("decodificados, milis " + (System.currentTimeMillis() - ini));

		Map<Card, Integer> qnts = new HashMap<>();
		for (Deck deck : decks) {
			for (Map.Entry<Card, Integer> e : deck.getCards().entrySet()) {
				Card cc = e.getKey();
				Integer i = e.getValue();
				if (qnts.get(cc) != null) {
					qnts.put(cc, qnts.get(cc) + i);
				} else {
					qnts.put(cc, i);
				}
			}
		}
		for (Map.Entry<Card, Integer> e : qnts.entrySet()) {
			System.out.println(e.getKey().getNome() + "\t" + e.getValue());
		}
	}

	/**
	 * Load meta decks.
	 */
	public void loadDecksRecursive(File dir) {
		File listOfFiles[] = dir.listFiles();
//		Arrays.sort(listOfFiles, new Comparator<File>() {
//			@Override
//			public int compare(File o1, File o2) {
//				return o1.getNome().compareTo(o2.getNome());
//			}
//		});
		for (File file : listOfFiles) {
			if (file.isDirectory()) {
				loadDecksRecursive(file);
			} else {
				parseDeckTGF(file);
			}
		}
		System.out.println(decks.size() + " decks loaded.");
	}

	private void parseDeckTGF(File file) {
		// Set<SynergyEdge<Deck, Card>> cards = new HashSet<>();
		// Map<Card, Integer> cartas = new LinkedHashMap<Card, Integer>();
		try {
			Scanner sc = new Scanner(file);
			String obs = null;
			Deck deck = new Deck(file.getName(), "");
			while (sc.hasNextLine()) {
				// Apenas para ceitar ctrl-c-v do
				// http://www.hearthstonetopdecks.com
				String line = sc.nextLine().replace("�", "'").replaceFirst("^(\\d+)(\\w)", "$1\t$2")
						.replaceFirst("(\\w)(\\d+)$", "$1\t$2");// .toLowerCase();
				if (line.startsWith("#")) {
					obs = line.substring(1, line.length());
				} else {
					String[] vals = line.split("\t");
					int i = 0;
					if (vals.length == 1)
						vals = line.split(";");
					if (vals.length > 2)
						i = 1;
					if (vals.length > 1 && !"".equals(vals[i]) && !"".equals(vals[i + 1])) {
						try {
							deck.getCards().put(cardComp.getCard(vals[i]), Integer.parseInt(vals[i + 1]));
						} catch (Exception rt) {
							deck.getCards().put(cardComp.getCard(vals[i + 1]), Integer.parseInt(vals[i]));
						}
					}
				}
			}
			deck.getStats().setArchtype(Archtype.values()[Integer.parseInt(obs)]);
			decks.add(deck);
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param data the base64 decoded data. This method intentionnaly does not
	 *             decode Base64 as implementation differ greatly between android
	 *             and other JVM. See https://github.com/auth0/java-jwt/issues/131
	 *             for more details
	 * @return
	 * @throws Exception
	 */
	// TODO calcular a expansao e epoca do deck pelas suas cartas mais recentes
	public Deck decodeDeckString(String encodedString) {
		if (!encodedString.startsWith("AAE")) {
			log.error("deck inválido: " + encodedString);
			return null;
		}
		byte[] data = Base64.getDecoder().decode(encodedString);
		// String decodedString = new String(data);
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		byteBuffer.get(); // reserverd
		int version = byteBuffer.get();
		if (version != 1) {
			// throw new ParseException("bad version: " + version);
		}
		Format formato = Format.getByValor(VarInt.getVarInt(byteBuffer));
		// Map<Card, Integer> cards = new HashMap<>();
//		Set<SynergyEdge<Deck, Card>> cards = new HashSet<SynergyEdge<Deck, Card>>();
//		if (result.format != FT_STANDARD && result.format != FT_WILD) {
//           throw new ParseException("bad format: " + result.format);
//		}

		int heroCount = VarInt.getVarInt(byteBuffer);
		Deck deck = null;
		for (int i = 0; i < heroCount; i++) {
			int cInt = VarInt.getVarInt(byteBuffer);
			if (i == 0) {
				// TODO chage getClasses to list?
				try {
					deck = new Deck("decoded deck",
							cardComp.getCard(String.valueOf(cInt)).getClasses().iterator().next(), encodedString);
				} catch (RuntimeException e) {
					log.error("Deck inválido. " + e.getLocalizedMessage() + ", DECK: " + encodedString);
					return null;
				}
			} else {
				// heroes are one of a kind
				try {
					deck.getCards().put(cardComp.getCard(String.valueOf(cInt)), 1);
				} catch (RuntimeException e) {
					log.error(e.getLocalizedMessage() + ", DECK: " + encodedString);
				}
			}
		}

		for (int i = 1; i <= 3; i++) {
			int c = VarInt.getVarInt(byteBuffer);
			for (int j = 0; j < c; j++) {
				int dbfId = VarInt.getVarInt(byteBuffer);
				int count;
				if (i == 3) {
					count = VarInt.getVarInt(byteBuffer);
				} else {
					count = i;
				}
				try {
					// cards.add(new SynergyEdge<>(deck,
					deck.getCards().put(cardComp.getCard(String.valueOf(dbfId)), count);
				} catch (RuntimeException e) {
					log.error(e.getLocalizedMessage() + ", DECK: " + encodedString);
				}
			}
		}
		// deck.setCards(cards);
		deck.setFormat(formato);
		// System.out.println(encodedString);
		// System.out.println("Deck decoded: " + deck);
		return deck;
	}
}

/**
 * Common methods to encode and decode varints and varlongs into ByteBuffers and
 * arrays.
 */
class VarInt {
	/**
	 * Maximum encoded size of 32-bit positive integers (in bytes)
	 */
	public static final int MAX_VARINT_SIZE = 5;

	/**
	 * maximum encoded size of 64-bit longs, and negative 32-bit ints (in bytes)
	 */
	public static final int MAX_VARLONG_SIZE = 10;

	private VarInt() {
	}

	/**
	 * Returns the encoding size in bytes of its input value.
	 * 
	 * @param i the integer to be measured
	 * @return the encoding size in bytes of its input value
	 */
	public static int varIntSize(int i) {
		int result = 0;
		do {
			result++;
			i >>>= 7;
		} while (i != 0);
		return result;
	}

	/**
	 * Reads a varint from src, places its values into the first element of dst and
	 * returns the offset in to src of the first byte after the varint.
	 *
	 * @param src    source buffer to retrieve from
	 * @param offset offset within src
	 * @param dst    the resulting int value
	 * @return the updated offset after reading the varint
	 */
	public static int getVarInt(byte[] src, int offset, int[] dst) {
		int result = 0;
		int shift = 0;
		int b;
		do {
			if (shift >= 32) {
				// Out of range
				throw new IndexOutOfBoundsException("varint too long");
			}
			// Get 7 bits from next byte
			b = src[offset++];
			result |= (b & 0x7F) << shift;
			shift += 7;
		} while ((b & 0x80) != 0);
		dst[0] = result;
		return offset;
	}

	/**
	 * Encodes an integer in a variable-length encoding, 7 bits per byte, into a
	 * destination byte[], following the protocol buffer convention.
	 *
	 * @param v      the int value to write to sink
	 * @param sink   the sink buffer to write to
	 * @param offset the offset within sink to begin writing
	 * @return the updated offset after writing the varint
	 */
	public static int putVarInt(int v, byte[] sink, int offset) {
		do {
			// Encode next 7 bits + terminator bit
			int bits = v & 0x7F;
			v >>>= 7;
			byte b = (byte) (bits + ((v != 0) ? 0x80 : 0));
			sink[offset++] = b;
		} while (v != 0);
		return offset;
	}

	/**
	 * Reads a varint from the current position of the given ByteBuffer and returns
	 * the decoded value as 32 bit integer.
	 *
	 * <p>
	 * The position of the buffer is advanced to the first byte after the decoded
	 * varint.
	 *
	 * @param src the ByteBuffer to get the var int from
	 * @return The integer value of the decoded varint
	 */
	public static int getVarInt(ByteBuffer src) {
		int tmp;
		if (!src.hasRemaining())
			return 0;
		if ((tmp = src.get()) >= 0) {
			return tmp;
		}
		int result = tmp & 0x7f;
		if ((tmp = src.get()) >= 0) {
			result |= tmp << 7;
		} else {
			result |= (tmp & 0x7f) << 7;
			if ((tmp = src.get()) >= 0) {
				result |= tmp << 14;
			} else {
				result |= (tmp & 0x7f) << 14;
				if ((tmp = src.get()) >= 0) {
					result |= tmp << 21;
				} else {
					result |= (tmp & 0x7f) << 21;
					result |= (tmp = src.get()) << 28;
					while (tmp < 0) {
						// We get into this loop only in the case of overflow.
						// By doing this, we can call getVarInt() instead of
						// getVarLong() when we only need an int.
						tmp = src.get();
					}
				}
			}
		}
		return result;
	}

	/**
	 * Encodes an integer in a variable-length encoding, 7 bits per byte, to a
	 * ByteBuffer sink.
	 * 
	 * @param v    the value to encode
	 * @param sink the ByteBuffer to add the encoded value
	 */
	public static void putVarInt(int v, ByteBuffer sink) {
		while (true) {
			int bits = v & 0x7f;
			v >>>= 7;
			if (v == 0) {
				sink.put((byte) bits);
				return;
			}
			sink.put((byte) (bits | 0x80));
		}
	}

	/**
	 * Reads a varint from the given InputStream and returns the decoded value as an
	 * int.
	 *
	 * @param inputStream the InputStream to read from
	 */
	public static int getVarInt(InputStream inputStream) throws IOException {
		int result = 0;
		int shift = 0;
		int b;
		do {
			if (shift >= 32) {
				// Out of range
				throw new IndexOutOfBoundsException("varint too long");
			}
			// Get 7 bits from next byte
			b = inputStream.read();
			result |= (b & 0x7F) << shift;
			shift += 7;
		} while ((b & 0x80) != 0);
		return result;
	}

	/**
	 * Encodes an integer in a variable-length encoding, 7 bits per byte, and writes
	 * it to the given OutputStream.
	 *
	 * @param v            the value to encode
	 * @param outputStream the OutputStream to write to
	 */
	public static void putVarInt(int v, OutputStream outputStream) throws IOException {
		byte[] bytes = new byte[varIntSize(v)];
		putVarInt(v, bytes, 0);
		outputStream.write(bytes);
	}

	/**
	 * Returns the encoding size in bytes of its input value.
	 *
	 * @param v the long to be measured
	 * @return the encoding size in bytes of a given long value.
	 */
	public static int varLongSize(long v) {
		int result = 0;
		do {
			result++;
			v >>>= 7;
		} while (v != 0);
		return result;
	}

	/**
	 * Reads an up to 64 bit long varint from the current position of the given
	 * ByteBuffer and returns the decoded value as long.
	 *
	 * <p>
	 * The position of the buffer is advanced to the first byte after the decoded
	 * varint.
	 *
	 * @param src the ByteBuffer to get the var int from
	 * @return The integer value of the decoded long varint
	 */
	public static long getVarLong(ByteBuffer src) {
		long tmp;
		if ((tmp = src.get()) >= 0) {
			return tmp;
		}
		long result = tmp & 0x7f;
		if ((tmp = src.get()) >= 0) {
			result |= tmp << 7;
		} else {
			result |= (tmp & 0x7f) << 7;
			if ((tmp = src.get()) >= 0) {
				result |= tmp << 14;
			} else {
				result |= (tmp & 0x7f) << 14;
				if ((tmp = src.get()) >= 0) {
					result |= tmp << 21;
				} else {
					result |= (tmp & 0x7f) << 21;
					if ((tmp = src.get()) >= 0) {
						result |= tmp << 28;
					} else {
						result |= (tmp & 0x7f) << 28;
						if ((tmp = src.get()) >= 0) {
							result |= tmp << 35;
						} else {
							result |= (tmp & 0x7f) << 35;
							if ((tmp = src.get()) >= 0) {
								result |= tmp << 42;
							} else {
								result |= (tmp & 0x7f) << 42;
								if ((tmp = src.get()) >= 0) {
									result |= tmp << 49;
								} else {
									result |= (tmp & 0x7f) << 49;
									if ((tmp = src.get()) >= 0) {
										result |= tmp << 56;
									} else {
										result |= (tmp & 0x7f) << 56;
										result |= ((long) src.get()) << 63;
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Encodes a long integer in a variable-length encoding, 7 bits per byte, to a
	 * ByteBuffer sink.
	 * 
	 * @param v    the value to encode
	 * @param sink the ByteBuffer to add the encoded value
	 */
	public static void putVarLong(long v, ByteBuffer sink) {
		while (true) {
			int bits = ((int) v) & 0x7f;
			v >>>= 7;
			if (v == 0) {
				sink.put((byte) bits);
				return;
			}
			sink.put((byte) (bits | 0x80));
		}
	}
}