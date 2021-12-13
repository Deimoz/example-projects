package markup;

import java.util.List;

public class Emphasis extends AbstractMarkup {
    public Emphasis(List<MarkDownElements> elements) {
        super(elements);
    }
	
	@Override
	String markupSymbol() {
		return "*";
	}
	
	@Override
	String htmlSymbol() {
		return "em";
	}
}
