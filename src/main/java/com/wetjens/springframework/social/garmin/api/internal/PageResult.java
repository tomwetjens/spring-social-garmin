package com.wetjens.springframework.social.garmin.api.internal;

public class PageResult<T> {

    private final T item;

    public PageResult(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}

