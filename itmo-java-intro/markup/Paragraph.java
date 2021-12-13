package markup;

import java.util.List;

public class Paragraph implements MarkDownElements {
    private List<MarkDownElements> elements;

    public Paragraph(List<MarkDownElements> elements) {
        this.elements = elements;
    }

	@Override
    public void toMarkdown(StringBuilder str) {
        for (MarkDownElements elem : elements) {
            elem.toMarkdown(str);
        }
    }
	
	@Override
	public void toHtml(StringBuilder str) {
        for (MarkDownElements elem : elements) {
            elem.toHtml(str);
        }
    }
}
