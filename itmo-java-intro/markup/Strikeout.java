package markup;

import java.util.List;

public class Strikeout extends AbstractMarkup {
    public Strikeout(List<MarkDownElements> elements) {
        super(elements);
    }
	
	@Override
	String markupSymbol() {
		return "~";
	}
	
	@Override
	String htmlSymbol() {
		return "s";
	}
}
