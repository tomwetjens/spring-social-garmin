package org.springframework.social.garmin.api;

import java.util.Iterator;

public interface Paging<T> extends Iterator<T> {

    int getTotalFound();

}
