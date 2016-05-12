package timely.store;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import timely.Configuration;
import timely.api.model.Meta;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class MetaCacheImpl implements MetaCache {

    private static final Object DUMMY = new Object();
    private volatile boolean closed = false;
    private Cache<Meta, Object> cache = null;

    @Override
    public void init(Configuration config) {
        long defaultExpiration = Long.parseLong(config.get(Configuration.META_CACHE_EXPIRATION));
        int initialCapacity = Integer.parseInt(config.get(Configuration.META_CACHE_INITIAL_CAPACITY));
        long maxCapacity = Long.parseLong(config.get(Configuration.META_CACHE_MAX_CAPACITY));
        cache = Caffeine.newBuilder().expireAfterAccess(defaultExpiration, TimeUnit.MINUTES)
                .initialCapacity(initialCapacity).maximumSize(maxCapacity).build();
    }

    @Override
    public void add(Meta meta) {
        cache.put(meta, DUMMY);
    }

    @Override
    public boolean contains(Meta meta) {
        return cache.asMap().containsKey(meta);
    }

    @Override
    public void addAll(Collection<Meta> c) {
        c.forEach(m -> cache.put(m, DUMMY));
    }

    @Override
    public Iterator<Meta> iterator() {
        return cache.asMap().keySet().iterator();
    }

    @Override
    public void close() {
        this.closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

}
