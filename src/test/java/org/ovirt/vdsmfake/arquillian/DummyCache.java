package org.ovirt.vdsmfake.arquillian;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.CacheCollection;
import org.infinispan.CacheSet;
import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.filter.KeyFilter;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.notifications.cachelistener.filter.CacheEventConverter;
import org.infinispan.notifications.cachelistener.filter.CacheEventFilter;
import org.ovirt.vdsmfake.domain.BaseObject;

@Singleton
public class DummyCache implements Cache<String, BaseObject> {

    public DummyCache() {
    }

    @Override public void putForExternalRead(String s, BaseObject baseObject) {

    }

    @Override public void putForExternalRead(String s, BaseObject baseObject, long l, TimeUnit timeUnit) {

    }

    @Override public void putForExternalRead(String s,
            BaseObject baseObject,
            long l,
            TimeUnit timeUnit,
            long l1,
            TimeUnit timeUnit1) {

    }

    @Override public void evict(String s) {

    }

    @Override public Configuration getCacheConfiguration() {
        return null;
    }

    @Override public EmbeddedCacheManager getCacheManager() {
        return null;
    }

    @Override public AdvancedCache<String, BaseObject> getAdvancedCache() {
        return null;
    }

    @Override public ComponentStatus getStatus() {
        return null;
    }

    @Override public int size() {
        return 0;
    }

    @Override public boolean isEmpty() {
        return false;
    }

    @Override public boolean containsKey(Object key) {
        return false;
    }

    @Override public boolean containsValue(Object value) {
        return false;
    }

    @Override public BaseObject get(Object key) {
        return null;
    }

    @Override public CacheSet<String> keySet() {
        return null;
    }

    @Override public CacheCollection<BaseObject> values() {
        return null;
    }

    @Override public CacheSet<Entry<String, BaseObject>> entrySet() {
        return null;
    }

    @Override public void clear() {

    }

    @Override public String getName() {
        return null;
    }

    @Override public String getVersion() {
        return null;
    }

    @Override public BaseObject put(String s, BaseObject baseObject) {
        return null;
    }

    @Override public BaseObject put(String s, BaseObject baseObject, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override public BaseObject putIfAbsent(String s, BaseObject baseObject, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override public void putAll(Map<? extends String, ? extends BaseObject> map, long l, TimeUnit timeUnit) {

    }

    @Override public BaseObject replace(String s, BaseObject baseObject, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override public boolean replace(String s, BaseObject baseObject, BaseObject v1, long l, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public BaseObject put(String s, BaseObject baseObject, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
        return null;
    }

    @Override public BaseObject putIfAbsent(String s,
            BaseObject baseObject,
            long l,
            TimeUnit timeUnit,
            long l1,
            TimeUnit timeUnit1) {
        return null;
    }

    @Override public void putAll(Map<? extends String, ? extends BaseObject> map,
            long l,
            TimeUnit timeUnit,
            long l1,
            TimeUnit timeUnit1) {

    }

    @Override
    public BaseObject replace(String s, BaseObject baseObject, long l, TimeUnit timeUnit, long l1, TimeUnit timeUnit1) {
        return null;
    }

    @Override public boolean replace(String s,
            BaseObject baseObject,
            BaseObject v1,
            long l,
            TimeUnit timeUnit,
            long l1,
            TimeUnit timeUnit1) {
        return false;
    }

    @Override public BaseObject remove(Object o) {
        return null;
    }

    @Override public void putAll(Map<? extends String, ? extends BaseObject> m) {

    }

    @Override public BaseObject putIfAbsent(String key, BaseObject value) {
        return null;
    }

    @Override public boolean remove(Object key, Object value) {
        return false;
    }

    @Override public boolean replace(String key, BaseObject oldValue, BaseObject newValue) {
        return false;
    }

    @Override public BaseObject replace(String key, BaseObject value) {
        return null;
    }

    @Override public NotifyingFuture<BaseObject> putAsync(String s, BaseObject baseObject) {
        return null;
    }

    @Override public NotifyingFuture<BaseObject> putAsync(String s, BaseObject baseObject, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override public NotifyingFuture<BaseObject> putAsync(String s,
            BaseObject baseObject,
            long l,
            TimeUnit timeUnit,
            long l1,
            TimeUnit timeUnit1) {
        return null;
    }

    @Override public NotifyingFuture<Void> putAllAsync(Map<? extends String, ? extends BaseObject> map) {
        return null;
    }

    @Override public NotifyingFuture<Void> putAllAsync(Map<? extends String, ? extends BaseObject> map,
            long l,
            TimeUnit timeUnit) {
        return null;
    }

    @Override public NotifyingFuture<Void> putAllAsync(Map<? extends String, ? extends BaseObject> map,
            long l,
            TimeUnit timeUnit,
            long l1,
            TimeUnit timeUnit1) {
        return null;
    }

    @Override public NotifyingFuture<Void> clearAsync() {
        return null;
    }

    @Override public NotifyingFuture<BaseObject> putIfAbsentAsync(String s, BaseObject baseObject) {
        return null;
    }

    @Override
    public NotifyingFuture<BaseObject> putIfAbsentAsync(String s, BaseObject baseObject, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override public NotifyingFuture<BaseObject> putIfAbsentAsync(String s,
            BaseObject baseObject,
            long l,
            TimeUnit timeUnit,
            long l1,
            TimeUnit timeUnit1) {
        return null;
    }

    @Override public NotifyingFuture<BaseObject> removeAsync(Object o) {
        return null;
    }

    @Override public NotifyingFuture<Boolean> removeAsync(Object o, Object o1) {
        return null;
    }

    @Override public NotifyingFuture<BaseObject> replaceAsync(String s, BaseObject baseObject) {
        return null;
    }

    @Override
    public NotifyingFuture<BaseObject> replaceAsync(String s, BaseObject baseObject, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override public NotifyingFuture<BaseObject> replaceAsync(String s,
            BaseObject baseObject,
            long l,
            TimeUnit timeUnit,
            long l1,
            TimeUnit timeUnit1) {
        return null;
    }

    @Override public NotifyingFuture<Boolean> replaceAsync(String s, BaseObject baseObject, BaseObject v1) {
        return null;
    }

    @Override public NotifyingFuture<Boolean> replaceAsync(String s,
            BaseObject baseObject,
            BaseObject v1,
            long l,
            TimeUnit timeUnit) {
        return null;
    }

    @Override public NotifyingFuture<Boolean> replaceAsync(String s,
            BaseObject baseObject,
            BaseObject v1,
            long l,
            TimeUnit timeUnit,
            long l1,
            TimeUnit timeUnit1) {
        return null;
    }

    @Override public NotifyingFuture<BaseObject> getAsync(String s) {
        return null;
    }

    @Override public boolean startBatch() {
        return false;
    }

    @Override public void endBatch(boolean b) {

    }

    @Override public void start() {

    }

    @Override public void stop() {

    }

    @Override public void addListener(Object o, KeyFilter<? super String> keyFilter) {

    }

    @Override public <C> void addListener(Object o,
            CacheEventFilter<? super String, ? super BaseObject> cacheEventFilter,
            CacheEventConverter<? super String, ? super BaseObject, C> cacheEventConverter) {

    }

    @Override public void addListener(Object o) {

    }

    @Override public void removeListener(Object o) {

    }

    @Override public Set<Object> getListeners() {
        return null;
    }
}
