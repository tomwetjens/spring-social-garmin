package com.wetjens.springframework.social.garmin.api.internal;

import java.util.List;

public class PageResults<T> {

    private int totalFound;
    private int currentPage;
    private int totalPages;

    private final List<? extends PageResult<T>> results;

    public PageResults(List<? extends PageResult<T>> results) {
        this.results = results;
    }

    public int getTotalFound() {
        return this.totalFound;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public List<? extends PageResult<T>> getResults() {
        return results;
    }
}
