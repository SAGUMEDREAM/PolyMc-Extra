package cc.thonly.polymc_extra.interfaces;

import net.minecraft.core.MappedRegistry;

@SuppressWarnings("unchecked")
public interface IMappedRegistry<T> {
    void polymc_extra$unfreeze();

    void polymc_extra$remove(T entry);

    static <T> IMappedRegistry<T> of(MappedRegistry<T> mappedRegistry) {
        return (IMappedRegistry<T>) mappedRegistry;
    }
}
