package lico.ilc.cnr.it;

import edu.berkeley.nlp.mt.Alignment;
import lico.ilc.cnr.it.action.TEIserializer;
import lico.ilc.cnr.it.model.Stringa;
import lico.ilc.cnr.it.model.StringaImpl;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import lico.ilc.cnr.it.utils.StringaBuilder;
import lico.ilc.cnr.it.utils.StringaHelper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import edu.berkeley.nlp.*;

public final class Aligner {

    private final String OK = "OK!";
    private final String csvPath = "target/classes/TALMUD-TRADUZIONE-ALL-CSV-31-01-2019.csv";
    //private final String xmlTemplate = "target/classes/ParallelCorpora-template.xml";
    //private final String xmlTemplate = "target/classes/ParallelCorpus-1-5-9-withLiteral-template.xml"; //"ParallelCorpus-1-5-9.xml"
    private final String xmlTemplate = "target/classes/ParallelCorpus-2-19-withLiteral-template.xml"; //"ParallelCorpus-1-5-9.xml"

    private Document TEIdoc = null;

    public Aligner() {

    }

    public static void main(String args[]) throws Exception {
        boolean pXML = false;
        boolean pBerkeley = true;

        Aligner aligner = new Aligner();

        // prepara XML da CSV
        if(pXML) {
            aligner.prepareXML("shabbat-qiddushin-WithLiteral-TEI.xml");
        }

        else if (pBerkeley) {
            //String teixmlFile = "target/classes/shabbat-qiddushin-WithLiteral-TEI.xml";
            String teixmlFile = "target/classes/Barakot-Rosh-Ta-WithLiteral-TEI.xml";


            double startparse = System.nanoTime();
            aligner.parseTEIdoc(teixmlFile);
            System.out.println(aligner.TEIdoc.selectSingleNode("//*[@xml:id='heb-1.1.1.1.1']").asXML());
            double endparse = System.nanoTime();

            double timeparse = endparse - startparse;
            System.out.println("TIME PARSE: " + timeparse / 1000000000);

            StringaBuilder buildsrc = new StringaBuilder();
            buildsrc.segNode(aligner.TEIdoc.selectSingleNode("//*[@xml:id='heb-1.1.1.1.1']"));
            Stringa src = buildsrc.buildFromNode();
            buildsrc = null;

            StringaBuilder buildtrg = new StringaBuilder();
            buildtrg.segNode(aligner.TEIdoc.selectSingleNode("//*[@xml:id='ita-1.1.1.1.1']"));
            Stringa trg = buildtrg.buildFromNode();
            buildtrg = null;

            boolean[][] alignment = aligner.align(src, trg);

            //aligner.printAlignment(alignment);

            // costruisci file per training berkeley aligner componente riga di comando jar
            double sFiletxt = System.nanoTime();
            aligner.toFileForBerkeley(aligner.TEIdoc);
            double eFiletxt = System.nanoTime();

            double timeFiletxt = eFiletxt - sFiletxt;

            System.out.println("tempo trascorso in secondi per preparare file txt: " + (timeFiletxt/1000000000));

        }

        else {
            System.err.println("Non ho niente da fare!!");
        }




    }

    private void toFileForBerkeley(Document doc) {
        FileWriter writer = null;
        List<Node> trattatiHeb = null;
        List<Node> trattatiItaLit = null;
        trattatiHeb = doc.selectNodes("//*/text[@type='source-text']/body/div[@type='trattato']");
        trattatiItaLit = doc.selectNodes("//*/text[@type='literal-translation']/body/div[@type='trattato']");

        for (Node trattato : trattatiHeb){
            String trattato_id =((Element)trattato).attributeValue("id");
            System.out.println("trattato id: " + ((Element)trattato).attribute("id"));
            File out = new File("trattato-"+trattato_id+"-heb.txt");

            Iterator<Element> segments = ((Element) trattato).element("ab").elementIterator();


            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(out));
                while (segments.hasNext()) {
                    bw.write(segments.next().getText());
                    bw.newLine();

                }

                bw.flush();
                bw.close();

            }catch(Exception e){
                    System.err.println(e);
                }

        }

        System.out.println(trattatiItaLit);

        for (Node trattato : trattatiItaLit){
            String trattato_id =((Element)trattato).attributeValue("id");
            File out = new File("trattato-"+trattato_id+"-ita.txt");

            Iterator<Element> segments = ((Element) trattato).element("ab").elementIterator();

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(out));
                while (segments.hasNext()) {
                    bw.write(segments.next().getText());
                    bw.newLine();

                }
                bw.flush();
                bw.close();
            }catch (Exception e){
                System.err.println(e);
            }
        }

    }

    private void printAlignment(boolean[][] alignment) {
        System.out.println("Matrice di allineamento tra stringa src e stringa trg (in rosso)");
        System.out.println("idealmente le parole src sono sulle righe e le parole trg sono sulle colonne");
        for(int i = 0; i<alignment.length; i++){
            for(int j = 0; j<alignment[i].length; j++){
                boolean a = alignment[i][j];
                if(a)
                    System.err.print("[ x ]");
                else
                    System.err.print("[ - ]");
            }
            System.err.print('\n');
        }

    }

    public void parseTEIdoc(String TEIxmlFile) {
        SAXReader XMLreader = new SAXReader();

        try {
            this.TEIdoc = XMLreader.read(TEIxmlFile);

        } catch (Exception e) {
            System.err.println("ERRORE nell'inizializzazione del documento TEI" + e);
        }
    }

    public String getOk() {
        return OK;
    }

    public String getCsvPath() {
        return csvPath;
    }

    public File getTemplateXML() {
        File template = null;
        try {

            template = new File(xmlTemplate);

        } catch (Exception e) {
            System.err.println(e);
        }
        return template;
    }

    public boolean[][] align(Stringa scr, Stringa trg) {
        System.out.println(scr);
        System.out.println(trg);

        System.out.println(String.format("src:[%s] <--> trg:[%s]", scr.getContent(), trg.getContent()));
        List<String> slist = new ArrayList<>();
        List<String> flist = new ArrayList<>();
        slist.add(scr.getContent());
        flist.add(trg.getContent());

        Alignment bAlignment = new Alignment(slist,flist);

        System.out.println("berkeley Alignment: " + bAlignment.output());



        boolean[][] ret = {
                {false, true, true, false, false, true},
                {false, false, false, true, true, false},
                {true, false, false, true, true, true},
                {true, false, false, false, true, false},
                {false, true, false, true, true, false},
                {false, false, false, false, true, false}
        };
        // new bollean[4][3]; righe X colonne
        return ret;
    }

    public Document prepareXML(String xmlname)throws Exception {

        FileReader reader;
        BufferedReader buffRead;
        List<String> csvLines = new ArrayList<>();
        PrintStream o = System.out;


        File csvFile = new File(this.getCsvPath());

        o.println(csvFile.toString());

        double start = System.nanoTime();

        reader = new FileReader(csvFile);
        buffRead = new BufferedReader(reader);


        buffRead.lines().forEach(s -> csvLines.add(s));

        double end = System.nanoTime();

        double periodElapsed = end - start;

        o.println("Lettura CSV - time in secondi  : " + (periodElapsed / 1000000000));


        double startFilter = System.nanoTime();
        //List<String> berakotLines = csvLines.stream().filter(StringaHelper.berakotPredicate).collect(Collectors.toList());
        List<String> shabLines = csvLines.stream().filter(StringaHelper.shabbatPredicate).collect(Collectors.toList());
        //List<String> roshLines = csvLines.stream().filter(StringaHelper.roshPredicate).collect(Collectors.toList());
        //List<String> taLines = csvLines.stream().filter(StringaHelper.taPredicate).collect(Collectors.toList());
        List<String> qidLines = csvLines.stream().filter(StringaHelper.qiddushinPredicate).collect(Collectors.toList());

        double endFilter = System.nanoTime();

        double timefilter = endFilter - startFilter;

        o.println("FILTRO TRATTATI time in secondi " + (timefilter / 1000000000));

        //o.println("SIZE Berakot " + berakotLines.size());
        //o.println("SIZE Rosh haShanà " + roshLines.size());
        //o.println("SIZE Ta‘anìt " + taLines.size());
        o.println("SIZE Shabbat " + shabLines.size());
        o.println("SIZE Qiddushin " + qidLines.size());


        //List<Stringa> stringheSrcBerakot;
        //List<Stringa> stringheSrcRosh;
        //List<Stringa> stringheSrcTa;
        List<Stringa> stringheSrcShabbat;
        List<Stringa> stringheSrcQiddushun;


        double startListeStringa = System.nanoTime();
        // prepara la lista di stringhe in base al trattato
        //stringheSrcBerakot = StringaHelper.prepareListForStringhe(berakotLines, Stringa::getHash);
        //stringheSrcRosh = StringaHelper.prepareListForStringhe(roshLines, Stringa::getHash);
        //stringheSrcTa = StringaHelper.prepareListForStringhe(taLines, Stringa::getHash);
        stringheSrcShabbat = StringaHelper.prepareListForStringhe(shabLines, Stringa::getHash);
        stringheSrcQiddushun = StringaHelper.prepareListForStringhe(qidLines, Stringa::getHash);
        double endListeStringa = System.nanoTime();

        double timeListeStringa =  endListeStringa - startListeStringa;

        o.println("LISTE STRINGHE TRATTATI time in secondi " + (timeListeStringa / 1000000000));


        // istanziare il TEIserializer per la generazione del documento TEI-XML

        TEIserializer serializer;
        //serializer = TEIserializer.init(this.getTemplateXML(), Arrays.asList(stringheSrcBerakot,stringheSrcRosh,stringheSrcTa));
        serializer = TEIserializer.init(this.getTemplateXML(), Arrays.asList(stringheSrcShabbat,stringheSrcQiddushun));

        Document TEIdoc;


        double startSerializer = System.nanoTime();
        TEIdoc = serializer.serialize();
        double endSerializer = System.nanoTime();




        FileWriter out = new FileWriter(xmlname);

        OutputFormat format = OutputFormat.createPrettyPrint();

        XMLWriter writer = new XMLWriter(out, format);

        writer.write(TEIdoc);

        writer.flush();

        System.out.println("FILE CREATO");

        double timeSerializer =  endSerializer - startSerializer;

        o.println("Serializzazione XML TRATTATI time in secondi " + (timeSerializer / 1000000000));

        return TEIdoc;


    }

}
