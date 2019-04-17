package lico.ilc.cnr.it.action;

import lico.ilc.cnr.it.model.Stringa;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

public class TEIserializer {

    public static final String seg = "seg";
    public static final String type = "type";
    public static final String stringa = "stringa";
    public static final String n = "n";
    public static final String xmlid = "xml:id";
    public static final String ana = "ana";

    Document document = null;
    List<List<Stringa>> listaStringhe;

    private TEIserializer() {

    }

    private TEIserializer(File template, List<List<Stringa>> ls) {

        System.out.println(template.toString());

        SAXReader XMLreader = new SAXReader();

        try {
            document = XMLreader.read(template);
            listaStringhe = ls;
        } catch (Exception e) {
            System.err.println("ERRORE nell'inizializzazione del serializer" + e);
        }

    }

    public static TEIserializer init(File template, List<List<Stringa>> stringhe) {
        TEIserializer serializer;
        serializer = new TEIserializer(template, stringhe);
        return serializer;
    }

    public Document serialize() {

        System.out.println("#Trattati: " + listaStringhe.size());

        for (List<Stringa> trattatoStringhe : listaStringhe
        ) {
            double startTrattatoXML = System.nanoTime();

            System.out.println("# stringhe trattato: " + trattatoStringhe.size());


            for (int j = 0; j < trattatoStringhe.size(); j++) { // int j = 0; j < trattatoStringhe.size(); j++
                long startTime = System.nanoTime();
                Stringa src = trattatoStringhe.get(j);
                Stringa trg = src.getRelative();
                Stringa lit = trg.getRelative();

                System.out.print("Elaboro Stringa " + src.getIndex());

                String index = src.getIndex();
                String[] parts = index.split("\\.");

                //System.out.print(String.format(" T(%S).C(%S).B(%S).U(%S).S(%S)", parts[0], parts[1], parts[2], parts[3], parts[4]));
                String selectTrattatoNode = String.format("//*[@xml:id='%s-%s']", src.getLanguage(), parts[0]);
                //System.out.print("XPATH Trattato: " + selectTrattatoNode);
                //Node nodeTrattatoSrc = document.selectSingleNode(selectTrattatoNode); // "//*[@xml:id = 'heb-1']"
                //Element elementTrattatoSrc = (Element) nodeTrattatoSrc;
                //Element addingStringaSrc = elementTrattatoSrc.addElement("seg");
                //  <seg type="stringa" n="1" xml:id="heb-1.1.1.1.1" ana=""></seg>

                ((Element)(document.selectSingleNode(selectTrattatoNode))).addElement(seg)
                .addAttribute(type,stringa)
                .addAttribute(n,src.getIndex().substring(src.getIndex().lastIndexOf('.') + 1))
                .addAttribute(xmlid, src.getLanguage() + "-" + src.getIndex())
                .addAttribute(ana, src.getHash().toString())
                .addText(src.getContent());

                System.out.print(" X");

                String indexTrg = trg.getIndex();
                String[] trgParts = indexTrg.split("\\.");

                String selectTrattatoNodeTrg = String.format("//*[@xml:id='%s-%s']", trg.getLanguage(), trgParts[0]);
                //System.out.print("XPATH Trattato TRG: " + selectTrattatoNodeTrg);

                Node nodeTrattatoTrg = document.selectSingleNode(selectTrattatoNodeTrg); // "//*[@xml:id = 'ita-1']"
                Element elementTrattatoTrg = (Element) nodeTrattatoTrg;
                Element addingStringTrg = elementTrattatoTrg.addElement("seg");
                //  <seg type="stringa" n="1" xml:id="heb-1.1.1.1.1" ana=""></seg>


                addingStringTrg.addAttribute(type, stringa);
                addingStringTrg.addAttribute(n, trg.getIndex().substring(trg.getIndex().lastIndexOf('.') + 1));
                addingStringTrg.addAttribute(xmlid, trg.getLanguage() + "-" + indexTrg);
                addingStringTrg.addAttribute(ana, trg.getHash().toString());
                addingStringTrg.addText(trg.getContent());

                System.out.print("X");

                String indexLit = lit.getIndex();
                String[] litParts = indexLit.split("\\.");

                String selectTrattatoNodeLit = String.format("//*[@xml:id='%s-literal-%s']", lit.getLanguage(), litParts[0]);
                //System.out.print(" XPATH Trattato LITTRG: " + selectTrattatoNodeLit);

                Node nodeTrattatoLit = document.selectSingleNode(selectTrattatoNodeLit); // "//*[@xml:id = 'ita-literal-1']"
                Element elementTrattatoLit = (Element) nodeTrattatoLit;
                Element addingStringLit = elementTrattatoLit.addElement("seg");
                //  <seg type="stringa" n="1" xml:id="heb-1.1.1.1.1" ana=""></seg>


                addingStringLit.addAttribute("type", "stringa");
                addingStringLit.addAttribute("n", trg.getIndex().substring(trg.getIndex().lastIndexOf('.') + 1));
                addingStringLit.addAttribute("xml:id", lit.getLanguage() + "-literal-" + indexLit);
                addingStringLit.addAttribute("ana", lit.getHash().toString());
                addingStringLit.addText(lit.getContent());

                System.out.print("X");

                String selectLinkNode = String.format("//*[@domains='#%s-%s #%s-%s #%s-literal-%s']", src.getLanguage(), parts[0], trg.getLanguage(), trgParts[0], lit.getLanguage(), litParts[0]);
                System.out.print("NODE LINK: " +selectLinkNode);
                Node nodeLink = document.selectSingleNode(selectLinkNode); // "//*[@domains='#heb-1 #ita-1']"
                Element elementLink = (Element) nodeLink;
                Element addingLink = elementLink.addElement("link");
                //  <seg type="stringa" n="1" xml:id="heb-1.1.1.1.1" ana=""></seg>

                addingLink.addAttribute("target", "#" + src.getLanguage() + "-" + src.getIndex() + " #" + trg.getLanguage() + "-" + trg.getIndex() + " #" + lit.getLanguage() + "-literal-" + lit.getIndex());
                //System.out.println("X FINE STRINGA " + src.getIndex());

                double endTime = System.nanoTime();

                double timeElapsed = endTime - startTime;

                System.out.println(" - GESTIONE STRINGA XML elapsed sec: " + timeElapsed / 1000000000);


            }

            double endTrattatoXML = System.nanoTime();

            double timeTrattatoXML = endTrattatoXML - startTrattatoXML;

            System.out.println(" - GESTIONE TRATTATO XML elapsed sec: " + timeTrattatoXML / 1000000000);

        }

        return document;


    }

}
