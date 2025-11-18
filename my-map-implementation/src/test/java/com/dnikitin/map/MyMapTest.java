package com.dnikitin.map;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.Map;
import java.util.stream.Stream;

import java.util.stream.Stream;

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
    public void getLeftSubtreeNodeElement(){
        map.put(2, "two");
        map.put(1, "one");

        assertEquals("one", map.get(1));
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


    /**
     * Tests AVL tree balancing for all rotation cases (LL, RR, LR, RL).
     * Regardless of insertion order, the resulting map should contain keys in sorted order (10, 20, 30)
     * and have a size of 3.
     */
    @ParameterizedTest(name = "{index} => insertionOrder={0}")
    @MethodSource("balancingCases")
    public void testBalancingAfterInsertions(List<Integer> insertionOrder) {
        // 1. Insert keys in the specific order provided by the test case
        for (Integer key : insertionOrder) {
            map.put(key, "val-" + key);
        }

        // 2. Verify the tree structure and size
        // The keys() method performs an in-order traversal, which must always result
        // in a sorted list (10, 20, 30) if the AVL tree is balanced correctly.
        List<Integer> expectedKeyOrder = List.of(10, 20, 30);

        assertAll(
                () -> assertEquals(expectedKeyOrder, map.keys(), "Keys should be sorted after balancing"),
                () -> assertEquals(3, map.size(), "Size should be 3")
        );
    }

    // Provides test data: keys to insert in specific orders to trigger rotations
    private static Stream<Arguments> balancingCases() {
        return Stream.of(
                Arguments.of(List.of(30, 20, 10)), // Left-Left Case -> rotateRight
                Arguments.of(List.of(10, 20, 30)), // Right-Right Case -> rotateLeft
                Arguments.of(List.of(30, 10, 20)), // Left-Right Case -> rotateLeftRight
                Arguments.of(List.of(10, 30, 20))  // Right-Left Case -> rotateRightLeft
        );
    }


    @Test
    public void putNullKeyOrValueShouldReturnFalse() {
        assertFalse(map.put(null, "value"));
        assertFalse(map.put(1, null));
        assertEquals(0, map.size());
    }

    @Test
    public void getNullKeyShouldReturnNull() {
        map.put(1, "one");
        assertNull(map.get(null));
    }

    @Test
    public void removeNullKeyShouldReturnFalse() {
        map.put(1, "one");
        assertFalse(map.remove(null));
        assertEquals(1, map.size());
    }

    @Test
    public void containsNullKeyShouldReturnFalse() {
        map.put(1, "one");
        assertFalse(map.contains(null));
    }

    @Test
    public void clearShouldRemoveAllElements() {
        map.put(1, "one");
        map.put(2, "two");
        map.clear();
        assertEquals(0, map.size());
        assertNull(map.get(1));
    }

    @Test
    public void iteratorShouldReturnKeysInOrder() {
        map.put(5, "five");
        map.put(1, "one");
        map.put(3, "three");

        List<Integer> keys = new ArrayList<>();
        for (java.util.Map.Entry<Integer, String> entry : map) {
            keys.add(entry.getKey());
        }

        assertEquals(List.of(1, 3, 5), keys);
    }

    @Test
    public void iteratorOnEmptyMapShouldWork() {
        assertFalse(map.iterator().hasNext());
    }

    @Test
    public void iteratorNextOnEmptyMapShouldThrowException() {
        Iterator<Map.Entry<Integer, String>> iterator = map.iterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void testRemoveNodeWithTwoChildren() {
        map.put(20, "A"); // root

        map.put(10, "B"); // left
        map.put(30, "C"); // right
        map.put(25, "D"); // right-left

        map.remove(20); // Remove root

        assertEquals(3, map.size());
        assertEquals(List.of(10, 25, 30), map.keys()); // Check structure
    }

    @Test
    public void testMapConstructor() {
        java.util.Map<Integer, String> javaMap = new java.util.HashMap<>();
        javaMap.put(1, "one");
        javaMap.put(2, "two");

        MyMap<Integer, String> newMap = new MyMap<>(javaMap);

        assertEquals(2, newMap.size());
        assertEquals("one", newMap.get(1));
        assertEquals("two", newMap.get(2));
    }

}
