package com.hush.app.bloom;


import org.springframework.stereotype.Component;

@Component
public class SimpleBloomFilter {
    private boolean[] switches = new boolean[10];

    private int getIndexA(String url) {
        return url.length() % 10;
    }

    private int getIndexB(String url) {
        return (int) url.charAt(0) % 10;
    }

    public void add(String url) {
        switches[getIndexA(url)] = true;
        switches[getIndexB(url)] = true;
    }

    public boolean isMaybeTaken(String url) {
        return switches[getIndexA(url)] && switches[getIndexB(url)];
    }
}