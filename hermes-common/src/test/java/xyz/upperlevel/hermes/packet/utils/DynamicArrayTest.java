package xyz.upperlevel.hermes.packet.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import xyz.upperlevel.hermes.util.DynamicArray;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class DynamicArrayTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void basicTest() {
        DynamicArray<Character> array = new DynamicArray<>(5);
        array.set(1, 'c');
        assertSame('c', array.get(1));

        array.set(2, 'c');
        array.set(5, 'z');
        assertSame('c', array.get(2));
        assertSame('c', array.get(1));
        assertSame('z', array.get(5));

        array.set(5, null);
        assertSame(null, array.get(5));

        array.set(55, 'x');
        array.set(99, 'y');
        assertSame('x', array.get(55));
        assertSame('y', array.get(99));
    }

    @Test
    public void hugeCapacityTest() {
        DynamicArray<String> array = new DynamicArray<>(4);
        array.set(1, "small");
        assertEquals("small", array.get(1));

        array.set(100, "quite big");
        assertEquals("quite big", array.get(100));

        array.set(100000, "a little bigger");
        assertEquals("a little bigger", array.get(100000));

        array.set(10000000, "a lot bigger");
        assertEquals("a lot bigger", array.get(10000000));

        exception.expect(IndexOutOfBoundsException.class);
        array.set(Integer.MAX_VALUE, "a little too big");
    }

    @Test
    public void negativeBoundSetTest() {
        exception.expect(IndexOutOfBoundsException.class);
        new DynamicArray<String>(5).set(-1, "Negative index?");
    }

    @Test
    public void negativeBoundGetTest() {
        exception.expect(IndexOutOfBoundsException.class);
        new DynamicArray<String>(5).get(-1);
    }


    @Test
    public void positiveBoundSetTest() {
        exception.expect(IndexOutOfBoundsException.class);
        new DynamicArray<String>(3, 5).set(7, "too much");
    }

    @Test
    public void positiveBoundGetTest() {
        exception.expect(IndexOutOfBoundsException.class);
        new DynamicArray<String>(3, 5).get(7);
    }

    @Test
    public void testIterator() {

        DynamicArray<Object> array = new DynamicArray<>(5, 20);
        array.set(1, '5');
        array.set(7, "seven");
        array.set(15, 66);
        array.set(10, '0');


        ListIterator<Object> it = array.iterator();
        try {
            while (true) {
                it.next();
            }
        } catch (NoSuchElementException ignored) {
        }

        it = array.iterator();
        assertEquals(
                array.get(0),
                it.next()
        );
        assertEquals(
                array.get(0),
                it.previous()
        );

        try {
            it.previous();
            fail();
        } catch (NoSuchElementException ignored) {
        }

        it.remove();

        try {
            it.add("null");
            fail();
        } catch (UnsupportedOperationException ignored) {
        }


        it = array.iterator(array.capacity() - 1);
        it.previous();
        assertEquals(
                it.next(),
                array.get(array.capacity() - 2)
        );
    }
}
