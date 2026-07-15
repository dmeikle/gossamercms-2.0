package com.gossamercms.auth.dtos.requests;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public class MutableHeaderRequest extends HttpServletRequestWrapper {

    private final Map<String, String> headers = new HashMap<>();

    public MutableHeaderRequest(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String value = headers.get(name);

        if (value != null) {
            return value;
        }

        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String value = headers.get(name);

        if (value != null) {
            return Collections.enumeration(List.of(value));
        }

        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>(headers.keySet());

        Enumeration<String> existing = super.getHeaderNames();

        while (existing.hasMoreElements()) {
            names.add(existing.nextElement());
        }

        return Collections.enumeration(names);
    }
}
