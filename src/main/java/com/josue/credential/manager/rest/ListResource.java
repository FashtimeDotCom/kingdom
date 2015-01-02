package com.josue.credential.manager.rest;

import java.util.ArrayList;
import java.util.List;

public class ListResource<T extends Resource> extends Resource {

    private long totalCount;
    private long offset;
    private long limit;
    private Resource first;
    private Resource last;
    private Resource next;
    private Resource prev;
    private List<T> items;

    public ListResource(List<T> items) {
        this.items = items;

    }

    public ListResource() {
        this.items = new ArrayList<>();
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public Resource getFirst() {
        return first;
    }

    public void setFirst(Resource first) {
        this.first = first;
    }

    public Resource getLast() {
        return last;
    }

    public void setLast(Resource last) {
        this.last = last;
    }

    public Resource getNext() {
        return next;
    }

    public void setNext(Resource next) {
        this.next = next;
    }

    public Resource getPrev() {
        return prev;
    }

    public void setPrev(Resource prev) {
        this.prev = prev;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
