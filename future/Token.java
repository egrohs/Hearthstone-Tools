package hstools.ai.future;

import java.util.Arrays;
import java.util.List;

public enum Token {
	PRONOME {
		@Override
		public List<Token> nextStates() {
			return Arrays.asList(new Token[] { TARGET, TIME, ZONE });
		}

		@Override
		public String responsiblePerson() {
			return "Employee";
		}
	},
	MECANICA {
		@Override
		public List<Token> nextStates() {
			return Arrays.asList(new Token[] { QNT, VERB });
		}

		@Override
		public String responsiblePerson() {
			return "Team Leader";
		}
	},
	TARGET {
		@Override
		public List<Token> nextStates() {
			return Arrays.asList(new Token[] { PREPOSITION, TARGET });
		}

		@Override
		public String responsiblePerson() {
			return "Department Manager";
		}
	},
	ZONE {
		@Override
		public List<Token> nextStates() {
			return Arrays.asList(new Token[] { PREPOSITION });
		}

		@Override
		public String responsiblePerson() {
			return "Department Manager";
		}
	},
	VERB {
		@Override
		public List<Token> nextStates() {
			return Arrays.asList(new Token[] { QNT });
		}

		@Override
		public String responsiblePerson() {
			return "Department Manager";
		}
	},
	ATTRIB {
		@Override
		public List<Token> nextStates() {
			return Arrays.asList(new Token[] { PREPOSITION, OPERATOR, TARGET/*??*/ });
		}

		@Override
		public String responsiblePerson() {
			return "Department Manager";
		}
	},
	TIME {
		@Override
		public List<Token> nextStates() {
			return Arrays.asList(new Token[] { PREPOSITION, VERB, QNT, TARGET });
		}

		@Override
		public String responsiblePerson() {
			return "Department Manager";
		}
	},
	QNT {
		@Override
		public List<Token> nextStates() {
			return Arrays.asList(new Token[] { PRONOME, ATTRIB, TARGET, QNT/*??*/ });
		}

		@Override
		public String responsiblePerson() {
			return "Department Manager";
		}
	},
	OPERATOR {
		@Override
		public List<Token> nextStates() {
			return Arrays.asList(new Token[] { ATTRIB, MECANICA });
		}

		@Override
		public String responsiblePerson() {
			return "Department Manager";
		}
	},
	PREPOSITION {
		@Override
		public List<Token> nextStates() {
			return Arrays.asList(new Token[] { QNT, PRONOME });
		}

		@Override
		public String responsiblePerson() {
			return "Department Manager";
		}
	},
	RACA {
		@Override
		public List<Token> nextStates() {
			return null;
		}

		@Override
		public String responsiblePerson() {
			return "Department Manager";
		}
	},;

	public abstract List<Token> nextStates();

	public abstract String responsiblePerson();
}