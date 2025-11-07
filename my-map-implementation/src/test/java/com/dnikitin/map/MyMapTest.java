package com.dnikitin.map;

import org.junit.jupiter.api.*;

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
}
