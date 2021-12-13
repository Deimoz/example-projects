package markup;

import java.util.List;

public class Strong extends AbstractMarkup {
    public Strong(List<MarkDownElements> elements) {
        super(elements);
    }
	
	@Override
	String markupSymbol() {
		return "__";
	}
	
	@Override
	String htmlSymbol() {
		return "strong";
	}
}
