package com.wetjens.springframework.social.garmin.api.internal;

import com.wetjens.springframework.social.garmin.api.Paging;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractPaging<T> implements Paging<T> {

    private final GarminConnectClient client;

    private final int limit;

    private int start;

    private PageResults<T> results;
    private Iterator<? extends PageResult<T>> resultIterator;

    public AbstractPaging(GarminConnectClient client, int start, int limit) {
        this.client = client;

        this.start = start;
        this.limit = limit;

        this.retrievePage();
    }

    protected GarminConnectClient getClient() {
        return client;
    }

    public boolean hasNext() {
        if (!this.resultIterator.hasNext() && this.start + this.limit < this.results.getTotalFound()) {
            // next page
            this.start += this.limit;

            this.retrievePage();
        }

        return this.resultIterator.hasNext();
    }

    private void retrievePage() {
        this.results = this.retrievePage(this.start, this.limit);
        this.resultIterator = this.results.getResults().iterator();
    }

    protected abstract PageResults<T> retrievePage(int start, int limit);

    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return this.resultIterator.next().getItem();
    }

    public int getTotalFound() {
        return this.results.getTotalFound();
    }
}
