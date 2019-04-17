package lico.ilc.cnr.it.model;

public interface Stringa {

    String getContent();
    String getIndex();
    String getLanguage();
    Long getHash();
    Stringa getRelative();

    Stringa setRelative(Stringa trg);
}
