package com.dnikitin.map;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class MyMapRunner {
    static void main() {
        java.util.Map<Integer, Integer> name = new TreeMap<>();
        MyMap<Integer, String> name2 = new MyMap<>();
        name2.put(1, "Krisik");
        name2.put(2, "Dimka");
        for (Map.Entry<Integer, String> entry : name2) {
            System.out.println("Klucz: " + entry.getKey() + ", Wartość: " + entry.getValue());
        }
    }
}
