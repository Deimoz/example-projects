package markup;

public class Text implements MarkDownElements {
    final private String standartElement;

    public Text(String standartElement) {
        this.standartElement = standartElement;
    }
	
	@Override
    public void toMarkdown(StringBuilder str) {
        str.append(standartElement);
    }
	
	@Override
	public void toHtml(StringBuilder str) {
		str.append(standartElement);
	}
}
