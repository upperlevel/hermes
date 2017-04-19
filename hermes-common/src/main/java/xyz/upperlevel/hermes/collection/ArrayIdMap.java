package xyz.upperlevel.hermes.collection;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

public class ArrayIdMap<V> implements IdMap<V> {
    private Object[] array;
    private final int expand;

    public ArrayIdMap(int len, int expand) {
        array = new Object[len];
        this.expand = expand;
    }

    public ArrayIdMap() {
        this(10, 10);
    }


    @Override
    public V put(int key, V value) {
        if(key < 0)
            throw new IllegalArgumentException("Index must be greater than 0");
        if(key >= array.length) {
            expand(key);
            array[key] = value;
            return null;
        } else {
            V old = get0(key);
            array[key] = value;
            return old;
        }
    }

    protected void expand(int min) {
        int newCap = (int) (Math.ceil((double)(min - array.length) / expand) * expand);
        array = Arrays.copyOf(array, newCap);
    }

    @Override
    public V get(int key) {
        if(key < 0)
            throw new IllegalArgumentException("Index must be greater than 0");
        return key >= array.length ? null : get0(key);
    }

    @SuppressWarnings("unchecked")
    protected V get0(int index) {
        return (V) array[index];
    }

    @Override
    public V remove(int key) {
        if(key < 0)
            throw new IllegalArgumentException("Index must be greater than 0");
        if(key < array.length) {
            V old = get0(key);
            array[key] = null;
            return old;
        } else return null;
    }

    @Override
    public V putIfAbsent(int key, V value) {
        if(key < 0)
            throw new IllegalArgumentException("Index must be greater than 0");
        if(key < array.length) {
            expand(key);
            array[key] = value;
            return null;
        } else {
            if(array[key] == null) {
                array[key] = value;
                return null;
            } else return get0(key);
        }
    }

    @Override
    public V computeIfAbsent(int key, IntFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        if(key < array.length) {
            expand(key);
            array[key] = mappingFunction.apply(key);
        } else if(array[key] == null)
            array[key] = mappingFunction.apply(key);
        return get0(key);
    }

    @Override
    public V computeIfPresent(int key, BiFunction<Integer, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);

        if(array[key] != null)
            array[key] = remappingFunction.apply(key, get0(key));

        return get0(key);
    }
}
