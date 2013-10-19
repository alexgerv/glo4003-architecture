package ca.ulaval.glo4003.index;

import java.util.List;

public interface Index<E extends Enum<E>> {

    public abstract void add(Indexable<E> indexable);

    public abstract List<String> getIdentifiersFor(E category, String filterOfCategory);

}