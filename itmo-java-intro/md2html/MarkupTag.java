package md2html;

public class MarkupTag {
    private String tag;
    private boolean isOpenTag;

    public MarkupTag(String tag, boolean isOpenTag) {
        this.tag = tag;
        this.isOpenTag = isOpenTag;
    }

    public String getTag() {
        return tag;
    }

    public boolean isOpen() {
        return isOpenTag;
    }
}
