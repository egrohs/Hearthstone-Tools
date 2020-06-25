package hstools.domain.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import hstools.Constants.Archtype;
import hstools.Constants.Format;
import hstools.domain.entities.Card;
import hstools.domain.entities.Deck;
import hstools.domain.entities.Tag;
import lombok.Getter;

/**
 * Load/unload downloaded decks from local files.
 * 
 * @author egrohs
 *
 */
@Service
@DependsOn(value = { "Cards" })
public class DeckComponent {
	@Autowired
	private CardComponent cardComp;

	@Getter
	private Set<Deck> decks = new LinkedHashSet<Deck>();

	public void calcStats(Deck deck) {
		for (Card c : deck.getCards().keySet()) {
			for (Tag t : c.getTags()) {
				deck.getTags().compute(t, (tokenKey, oldValue) -> oldValue == null ? deck.getCards().get(c)
						: oldValue + deck.getCards().get(c));
			}
		}
		for (Tag t : deck.getTags().keySet()) {
			if (t.getName().equals("HARD_REMOVE")) {
				deck.getStats().incHard_remove(deck.getTags().get(t));
			} else if (t.getName().equals("SOFT_REMOVE")) {// TODO rever, reduce attack...
				deck.getStats().incSoft_remove(deck.getTags().get(t));
			} else if (t.getName().equals("DRAW") || t.getName().equals("GENERATE")) {
				deck.getStats().incCard_adv(deck.getTags().get(t));
			} else if (t.getName().equals("LOW_COST_MINION")) {
				deck.getStats().incLow_cost_minions(deck.getTags().get(t));
			} else if (t.getName().equals("TAUNT") || t.getName().equals("LIFESTEAL") || t.getName().equals("ARMOR")
					|| t.getName().equals("HEALTH_RESTORE")) {
				deck.getStats().incSurv(deck.getTags().get(t));
			}
		}
		int acum = 0;
		for (Card c : deck.getCards().keySet()) {
			acum += c.getCost();
			if (c.getType().equals("minion")) {
				deck.getStats().setQnt_minions(deck.getStats().getQnt_minions() + 1);
			}
//				if (c.getRank() < 3.3) {
//					low_rank += cartas.get(c);
//				}
			// TODO spells
			if (c.getAttack() >= 8 || (c.getTags().toString().contains("WINDFURY") && c.getAttack() >= 3)
					|| (c.getTags().toString().contains("CHARGE") && c.getAttack() >= 5)) {
				deck.getStats().setFinishers(deck.getStats().getFinishers() + 1);
			}
		}
		deck.getStats().setAvg_mana(acum / 30.0);
//		System.out.print(deck.getName() + "\t");
//		System.out.print(deck.getLow_cost_minions() + ",");
//			if (hard_remove >= 1 && hard_remove <= 2) {
//				System.out.println("hard_remove = " + hard_remove + " ref = 1 a 2 control|destroy|shuffle|transform");
//			}
//			if (soft_remove >= 4 && soft_remove <= 8) {
//				System.out.println("soft_remove = " + soft_remove + " ref = 4 a 8 deal \\d+ damage|silence|return to");
//			}
//			if (ones >= 2 && ones <= 10) {
//				System.out.println("ones = " + ones + " ref = 2 a 10");
//			}
//			if (twos >= 2 && twos <= 8) {
//				System.out.println("twos = " + twos + " ref = 2 a 8");
//			}
//			if (threes >= 4 && threes <= 10) {
//				System.out.println("threes = " + threes + " ref = 4 a 10");
//			}
//			if (fours >= 4 && fours <= 6) {
//				System.out.println("fours = " + fours + " ref = 4 a 6");
//			}
//			if (fives >= 1 && fives <= 6) {
//				System.out.println("fives = " + fives + " ref = 1 a 6");
//			}
//			if (sixes >= 0 && sixes <= 4) {
//				System.out.println("sixes = " + sixes + " ref = 0 a 4");
//			}
//		System.out.print(avg_mana + ",");
//		System.out.print(card_adv + ",");
//		System.out.print(surv + ",");
//		System.out.println(archtype);
//			if (avg_mana < 3) {// tem control warrior e priest com avg_mana = 3
//				System.out.println("AGGRO");
//			} else if (avg_mana >= 4) {
//				// System.out.print(hard_remove + soft_remove + ",");
//				System.out.println("CONTROL");
//			} else {
//				// System.out.print(hard_remove + soft_remove + ",");
//				System.out.println("MIDRANGE");
//			}
	}

	public void unloadDeck(String deckString) {
		// TODO Auto-generated method stub

	}

	public void decodeDecksFromFile(String fname) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File("src/main/resources/decks/deckStrings/" + fname));
			while (sc.hasNextLine()) {
				String[] line = sc.nextLine().split("\t");
				// System.out.println(deckstr);
				Deck deck = decodeDeckString(line[0]);
				if (line.length > 1)
					deck.getStats().setArchtype(Archtype.values()[Integer.parseInt(line[1])]);
				decks.add(deck);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}

	/**
	 * Load meta decks.
	 */
	public void loadDecks(File dir) {
		File listOfFiles[] = dir.listFiles();
//		Arrays.sort(listOfFiles, new Comparator<File>() {
//			@Override
//			public int compare(File o1, File o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
		for (File file : listOfFiles) {
			if (file.isDirectory()) {
				loadDecks(file);
			} else {
				Map<Card, Integer> cartas = new LinkedHashMap<Card, Integer>();
				try {
					Scanner sc = new Scanner(file);
					String obs = null;
					while (sc.hasNextLine()) {
						// Apenas para ceitar ctrl-c-v do
						// http://www.hearthstonetopdecks.com
						String line = sc.nextLine().replaceAll("﻿", "").replaceAll("�", "'")
								.replaceFirst("^(\\d+)(\\w)", "$1\t$2").replaceFirst("(\\w)(\\d+)$", "$1\t$2");// .toLowerCase();
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
									cartas.put(cardComp.getCard(vals[i]), Integer.parseInt(vals[i + 1]));
								} catch (Exception rt) {
									cartas.put(cardComp.getCard(vals[i + 1]), Integer.parseInt(vals[i]));
								}
							}
						}
					}
					Deck deck = new Deck(file.getName(), cartas);
					deck.getStats().setArchtype(Archtype.values()[Integer.parseInt(obs)]);
					decks.add(deck);
					sc.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(decks.size() + " decks loaded.");
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
		byte[] data = Base64.getDecoder().decode(encodedString);
		// String decodedString = new String(data);
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		byteBuffer.get(); // reserverd
		int version = byteBuffer.get();
		if (version != 1) {
			// throw new ParseException("bad version: " + version);
		}

		Format formato = Format.getByValor(VarInt.getVarInt(byteBuffer));
		Map<Card, Integer> cartas = new HashMap<>();
//		if (result.format != FT_STANDARD && result.format != FT_WILD) {
//           throw new ParseException("bad format: " + result.format);
//		}

		int heroCount = VarInt.getVarInt(byteBuffer);
		// result.heroes = new ArrayList<>();
		for (int i = 0; i < heroCount; i++) {
			// result.heroes.add(VarInt.getVarInt(byteBuffer));
			// TODO pegar instancia do cardbuilder
			cartas.put(cardComp.getCard(String.valueOf(VarInt.getVarInt(byteBuffer))), 1);
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
				cartas.put(cardComp.getCard(String.valueOf(dbfId)), count);
			}
		}
		Deck deck = new Deck("", cartas);
		deck.setFormat(formato);
		System.out.println("Deck decoded: " + deck);
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