package cc.thonly.polymc_extra.util;

import com.google.common.collect.Multimap;
import com.mojang.authlib.properties.Property;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SPMultimapProxy implements Multimap<String, Property> {
    @Delegate
    private final Multimap<String, Property> delegate;

    public SPMultimapProxy(Multimap<String, Property> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean put(String key, Property value) {
        if (this.delegate.size() + 1 > 16) {
            System.out.printf("Error profile: %s%n", new Object().hashCode());
            System.out.println(key);
            System.out.println(value);
            Thread.dumpStack();
        }
        return this.delegate.put(key, value);
    }

}
