package lico.ilc.cnr.it.utils;

import lico.ilc.cnr.it.model.Stringa;
import lico.ilc.cnr.it.model.StringaImpl;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class StringaBuilder {

    private Stringa s = null;


    private String content;
    private String idx;
    private String lang;
    private Long hash;

    private Element segNode;


    public StringaBuilder() {

    }

    public StringaBuilder content(String content) {
        this.content = content;
        return this;
    }

    public StringaBuilder idx(String idx) {
        this.idx = idx;
        return this;
    }

    public StringaBuilder lang(String lang) {
        this.lang = lang;
        return this;
    }

    public StringaBuilder hash(Long hash) {
        this.hash = hash;
        return this;
    }

    public StringaBuilder segNode(Node sNode){
        this.segNode = (Element) sNode;
        return this;
    }


    public Stringa build() {
        try {

            String contentStripped;
            contentStripped = content.replaceAll("<span class=\"\"note\"\" [^>]* rel=.*?>.</span>", "");
            contentStripped = contentStripped.replaceAll("<[^>]*?>", "");

            //System.out.println(String.format("testo  = (%s); indice = (%s); hash = (%d) ", content, idx, hash));

            return StringaImpl.init(contentStripped, idx, lang, hash, null);


        } catch (NullPointerException ne) {
            System.out.println("Alcuni valori della stringa non sono stati popolati correttamente");
            return null;
        }
    }

    public Stringa buildLiteral(){
        try {

            String contentLiteral;
            String contentStripped = content.replaceAll("<span class=\"\"note\"\" [^>]* rel=.*?>.</span>", "");
            Document doc = Jsoup.parse(contentStripped);
            Elements literal = doc.select("span.bold");
            contentLiteral = literal.text();
            //System.out.println("build literal document: " + doc.html());
            //System.out.println("build literal content: " + content);
            return  StringaImpl.init(contentLiteral, idx, lang, hash, null);


        } catch (Exception e) {
            System.err.println("Alcuni valori della stringa non sono stati popolati correttamente" + e);
            return null;
        }

    }

    public Stringa buildFromNode() {
        Stringa ret = null;

        this.content = segNode.getText();
        this.hash = Long.parseLong(segNode.attributeValue("ana"));
        this.idx = segNode.attributeValue("id");
        this.lang = this.idx.substring(0,3);

        ret = StringaImpl.init(content, idx, lang, hash, null);
        return ret;
    }

}

