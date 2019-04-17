package lico.ilc.cnr.it.utils;

import lico.ilc.cnr.it.model.Stringa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringaHelper {

    public static final Predicate<String> berakotPredicate = line -> {
        String[] cs = line.split("\\t");
        boolean ret =  false;

        String h = "";
        long hash = -1;
        long den = -1;
        long t  = -1;

        try {

            h = cs[10];
            hash = Long.parseLong(h);
            den = Long.parseLong("1000000000000");
            t = hash/den;
            //System.out.print(h+" (B) -> hashcode - t: ");
            //System.out.println(t);

            ret = t==1;

        } catch(Exception e){

            System.err.print("err: "+e);
            System.err.print(" str h: "+h);
            System.err.print(" num hash: "+hash);
            System.err.print(" den: "+den);
            System.err.print(" t: "+t);
            System.err.println(" ret: "+ret);

        }


        return ret;
    };

    public static final Predicate<String> shabbatPredicate = line -> {
        String[] cs = line.split("\\t");
        boolean ret =  false;

        String h = "";
        long hash = -1;
        long den = -1;
        long t  = -1;

        try {

            h = cs[10];
            hash = Long.parseLong(h);
            den = Long.parseLong("1000000000000");
            t = hash/den;
            //System.out.print(h+" (B) -> hashcode - t: ");
            //System.out.println(t);

            ret = t==2;

        } catch(Exception e){

            System.err.print("err: "+e);
            System.err.print(" str h: "+h);
            System.err.print(" num hash: "+hash);
            System.err.print(" den: "+den);
            System.err.print(" t: "+t);
            System.err.println(" ret: "+ret);

        }


        return ret;
    };




    public static final Predicate<String> roshPredicate = line -> {
        String[] cs = line.split("\\t");
        boolean ret =  false;
        String h = "";
        long hash = -1;
        long den = -1;
        long t  = -1;

        try {
            h = cs[10];
            hash = Long.parseLong(h);
            den = Long.parseLong("1000000000000");
            t = hash/den;
            //System.out.print(h+" (R) -> hashcode - t: ");
            //System.out.println(t);

            ret = t==5;

        } catch(Exception e){

            System.err.print("err: "+e);
            System.err.print(" str hash: "+h);
            System.err.print(" hash: "+hash);
            System.err.print(" den: "+den);
            System.err.print(" t: "+t);
            System.err.println(" ret: "+ret);

        }


        return ret;
    };

    public static final Predicate<String> taPredicate = line -> {
        String[] cs = line.split("\\t");
        boolean ret =  false;
        String h = "";
        long hash = -1;
        long den = -1;
        long t  = -1;

        try {
            h = cs[10];
            hash = Long.parseLong(h);
            den = Long.parseLong("1000000000000");
            t = hash/den;
            //System.out.print(h+" (T) -> hashcode - t: ");
            //System.out.println(t);

            ret = t==9;

        } catch(Exception e){

            System.err.print("err: "+e);
            System.err.print(" str hash: "+h);
            System.err.print(" hash: "+hash);
            System.err.print(" den: "+den);
            System.err.print(" t: "+t);
            System.err.println(" ret: "+ret);

        }


        return ret;
    };

    public static final Predicate<String> qiddushinPredicate = line -> {
        String[] cs = line.split("\\t");
        boolean ret =  false;

        String h = "";
        long hash = -1;
        long den = -1;
        long t  = -1;

        try {

            h = cs[10];
            hash = Long.parseLong(h);
            den = Long.parseLong("1000000000000");
            t = hash/den;
            //System.out.print(h+" (B) -> hashcode - t: ");
            //System.out.println(t);

            ret = t==19;

        } catch(Exception e){

            System.err.print("err: "+e);
            System.err.print(" str h: "+h);
            System.err.print(" num hash: "+hash);
            System.err.print(" den: "+den);
            System.err.print(" t: "+t);
            System.err.println(" ret: "+ret);

        }


        return ret;
    };


    public static List<Stringa> prepareListForStringhe(List<String> csvLines, Function<Stringa,Long> comparatorMethod){

        List<Stringa> stringheTrattato = new ArrayList<>();

        for(int i = 0; i<csvLines.size(); i++){
            //System.out.println("elaboro riga " +i);
            String line = csvLines.get(i);
            //System.out.println("stampo la riga " +line);
            String[] fields = line.split("\\t");
            String orig = null;
            String target = null;
            String idx = null;
            Long hash = null;

            StringaBuilder sb = null;
            Stringa src = null;
            Stringa trg = null;
            Stringa trgLiteral = null;

            try {
                orig = fields[4];
                target = fields[5];
                idx = fields[8];
                hash = Long.parseLong(fields[10]);

            } catch (Exception e) {
                System.out.println("errore nei campi csv"+ e);
            }

            sb = new StringaBuilder();

            sb.lang("heb");
            sb.idx(idx);
            sb.hash(hash);
            sb.content(orig);

            src = sb.build();

            sb = null;
            sb = new StringaBuilder();

            sb.lang("ita");
            sb.idx(idx);
            sb.hash(hash);
            sb.content(target);

            trg = sb.build();
            trgLiteral = sb.buildLiteral();

            src.setRelative(trg);
            trg.setRelative(trgLiteral);
            trgLiteral.setRelative(src);

            //System.out.println("src - " + src);

            //System.out.println("trg - " + src.getRelative());

            //System.out.println("lit - " + src.getRelative().getRelative());

            sb = null;

            stringheTrattato.add(src);
        }

        Collections.sort(stringheTrattato, Comparator.comparing(comparatorMethod));

        return stringheTrattato;

    }
}
