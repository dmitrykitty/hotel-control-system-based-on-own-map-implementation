package com.dnikitin.map;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyMapTest {

    private MyMap<Integer, String> map;

    @BeforeEach
    public void setUp(){
        map = new MyMap<>();
    }

    @Test
    public void emptyIfNoElementsAdded(){
        assertTrue(map.isEmpty());
    }

    @Test
    public void sizeIsZeroIfNoElementsAdded(){
        assertEquals(0, map.size());
    }

    @Test
    public void sizeIfUniqueElementsAdded(){
        map.put(1, "one");
        map.put(2, "two");

        assertEquals(2, map.size());
    }

    @Test
    public void sizeIfDuplicateElementsAdded(){
        map.put(1, "one");
        map.put(2, "two");
        map.put(1, "one");

        assertEquals(2, map.size());
    }

    @Test
    public void sizeIfExistingElementRemoved(){
        map.put(1, "one");
        map.put(2, "two");
        map.remove(1);

        assertEquals(1, map.size());
    }

    @Test
    public void sizeIfNonExistingElementRemoved(){
        map.put(1, "one");
        map.put(2, "two");
        map.remove(3);

        assertEquals(2, map.size());
    }

    @Test
    public void emptyIfAllElementsRemoved(){
        map.put(1, "one");
        map.put(2, "two");
        map.remove(1);
        map.remove(2);

        assertTrue(map.isEmpty());
    }

    @Test
    public void getExistingElement(){
        map.put(1, "one");
        map.put(2, "two");

        assertEquals("one", map.get(1));
    }

    @Test
    public void getNonExistingElement(){
        map.put(1, "one");
        map.put(2, "two");

        assertNull(map.get(3));
    }

    @Test
    public void containsExistingElement(){
        map.put(1, "one");

        assertTrue(map.contains(1));
    }

    @Test
    public void containsNonExistingElement(){
        map.put(1, "one");

        assertFalse(map.contains(2));
    }

    @Test
    public void getListOfKeysOfEmptyMap(){
        assertTrue(map.keys().isEmpty());
    }

    @Test
    public void getListOfKeysOfNonEmptyMap(){
        List<Integer> expectedKeys = List.of(1, 2, 3);

        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        assertEquals(expectedKeys, map.keys());
    }

    //Testing of AVL private methods correct working
    @Test
    public void testBalancingAfterLeftLeftCase() {
        map.put(30, "A");
        map.put(20, "B");
        map.put(10, "C"); // rotateRight()


        List<Integer> expectedKeyOrder = List.of(10, 20, 30);
        assertEquals(expectedKeyOrder, map.keys());
        assertEquals(3, map.size());
    }

    @Test
    public void testBalancingAfterRightRightCase() {
        map.put(10, "A");
        map.put(20, "B");
        map.put(30, "C"); // rotateLeft()

        List<Integer> expectedKeyOrder = List.of(10, 20, 30);
        assertEquals(expectedKeyOrder, map.keys());
        assertEquals(3, map.size());
    }

    @Test
    public void testBalancingAfterLeftRightCase() {
        map.put(30, "A");
        map.put(10, "B");
        map.put(20, "C"); // rotateLeftRight()

        List<Integer> expectedKeyOrder = List.of(10, 20, 30);
        assertEquals(expectedKeyOrder, map.keys());
        assertEquals(3, map.size());
    }

    @Test
    public void testBalancingAfterRightLeftCase() {
        map.put(10, "A");
        map.put(30, "B");
        map.put(20, "C"); // rotateRightLeft()

        List<Integer> expectedKeyOrder = List.of(10, 20, 30);
        assertEquals(expectedKeyOrder, map.keys());
        assertEquals(3, map.size());
    }

}
