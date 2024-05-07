package hstools.ui;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataDTO {
	private String name;
	private String text;

	public DataDTO(String string, String string2) {
		this.name = string;
		this.text = string2;
	}
}