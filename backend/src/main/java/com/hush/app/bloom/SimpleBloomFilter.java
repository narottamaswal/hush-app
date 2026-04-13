package com.hush.app.bloom;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class SimpleBloomFilter {
    private static final int FILTER_SIZE = 8192;
    private boolean[] switches = new boolean[FILTER_SIZE];

    private int getIndexA(String url) {
        return Math.abs(url.hashCode()) % FILTER_SIZE; // Use hashCode for better distribution
    }

    private int getIndexB(String url) {
        int hash = 7;
        for (int i = 0; i < url.length(); i++) {
            hash = hash * 31 + url.charAt(i); // Custom hash function
        }
        return Math.abs(hash) % FILTER_SIZE;
    }

    public void add(String url) {
        if(!StringUtils.isBlank(url)){
            switches[getIndexA(url)] = true;
            switches[getIndexB(url)] = true;
        }
    }

    public boolean isMaybeTaken(String url) {
        return switches[getIndexA(url)] && switches[getIndexB(url)];
    }
}