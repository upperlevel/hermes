package xyz.upperlevel.utils.packet.collection;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

public interface IdMap<V> {
    V put(int key, V value);

    V get(int key);

    V remove(int key);

    default boolean containsKey(int key) {
        return get(key) != null;
    }

    default V putIfAbsent(int key, V value) {
        V v = get(key);
        if (v == null)
            v = put(key, value);

        return v;
    }

    default boolean replace(int key, V oldValue, V newValue) {
        Object curValue = get(key);
        if (!Objects.equals(curValue, oldValue) ||
                (curValue == null && !containsKey(key))) {
            return false;
        }
        put(key, newValue);
        return true;
    }

    default V computeIfAbsent(int key, IntFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null) {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

    default V computeIfPresent(int key, BiFunction<Integer, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue;
        if ((oldValue = get(key)) != null) {
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            } else {
                remove(key);
                return null;
            }
        } else {
            return null;
        }
    }


}
