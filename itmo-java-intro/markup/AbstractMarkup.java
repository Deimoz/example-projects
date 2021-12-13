package markup;

import java.util.List;

public abstract class AbstractMarkup implements MarkDownElements {
    private List<MarkDownElements> elements;

    public AbstractMarkup(List<MarkDownElements> elements) {
        this.elements = elements;
    }
	
	protected abstract String markupSymbol();
	
	@Override
    public void toMarkdown(StringBuilder str) {
        str.append(markupSymbol());
        for (MarkDownElements elem : elements) {
            elem.toMarkdown(str);
        }
        str.append(markupSymbol());
    }
	
	protected abstract String htmlSymbol();
	
	@Override
	public void toHtml(StringBuilder str) {
		str.append("<").append(htmlSymbol()).append('>');
		for (MarkDownElements elem : elements) {
            elem.toHtml(str);
        }
        str.append("</").append(htmlSymbol()).append(">");
	}
}