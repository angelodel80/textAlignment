package lico.ilc.cnr.it.model;

public class StringaImpl implements Stringa {

    private String content;
    private String index;
    private String language;
    private Long hash;

    private Stringa relative;


    private StringaImpl(){
        super();
    }

    private StringaImpl(String c, String i, String l, Long h, Stringa r){
        super();

        this.content = c;
        this.index = i;
        this.language = l;
        this.hash = h;

        this.relative = r;
    }

    public static Stringa init(String c, String i, String l, Long h, Stringa r){
        Stringa str = new StringaImpl(c, i, l, h, r);
        return str;

    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getIndex() {
        return index;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public Long getHash() {
        return hash;
    }

    @Override
    public Stringa getRelative() {
        return relative;
    }



    @Override
    public Stringa setRelative(Stringa trg) {
        this.relative = trg;
        return this;
    }


    @Override
    public String toString() {
        String relCont = (null!=relative)?relative.getContent():" ";

        return "Stringa{" +
                "content='" + content + '\'' +
                ", index='" + index + '\'' +
                ", language='" + language + '\'' +
                ", hash=" + hash +
                ", relative=" + relCont +
                '}';
    }
}
