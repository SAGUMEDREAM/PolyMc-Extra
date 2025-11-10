package cc.thonly.polymc_extra.interfaces;

public interface IMappedRegistry<T> {
    void unfreeze();
    void remove(T entry);
}
