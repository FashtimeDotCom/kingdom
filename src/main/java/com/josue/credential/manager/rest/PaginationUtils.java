package com.josue.credential.manager.rest;

public class PaginationUtils {

    private int limit;
    private long offset;

    public PaginationUtils(Integer limit, Long offset) {
        this.offset = offset != null ? offset : 0;
        this.limit = limit != null ? limit : 50;
        if (this.limit > 50) {
            this.limit = 50;
        }
    }

    public PaginationUtils(int limit, int offset) {
        this(limit, new Long(offset));
    }

    public void fillPagination(ListResource<? extends Resource> res,
            Long totalCount) {

        if (totalCount == null) {
            long first = 0;
            long previous = offset - limit;
            long next = offset + limit;
            if (previous < 0) {
                previous = 0;
            }

            if (offset != 0) {
                res.setPrev(Resource.fromHref("offset=" + previous + "&limit="
                        + limit));
            }
            res.setFirst(Resource.fromHref("offset=" + first + "&limit="
                    + limit));
            res.setNext(Resource.fromHref("offset=" + next + "&limit=" + limit));
        } else {
            long first = 0;
            long last = (totalCount - offset) / limit * limit + offset;
            if (last == totalCount) {
                last = last - limit;
            }
            long next = offset + limit;
            long previous = offset - limit;
            if (previous < 0) {
                previous = 0;
            }

            if (limit >= totalCount) { // less than one page
                if (offset != 0) {
                    res.setPrev(Resource.fromHref("offset=" + previous
                            + "&limit=" + limit));
                }
                res.setNext(null);
                res.setLast(Resource.fromHref("offset=" + first + "&limit="
                        + limit));
                res.setFirst(Resource.fromHref("offset=" + first + "&limit="
                        + limit));

            } else if (offset != 0 && (offset + limit) >= totalCount) { // last

                res.setPrev(Resource.fromHref("offset=" + previous + "&limit="
                        + limit));
                res.setNext(null);
                res.setLast(Resource.fromHref("offset=" + last + "&limit="
                        + limit));
                res.setFirst(Resource.fromHref("offset=" + first + "&limit="
                        + limit));
            } else if (offset == 0 && limit < totalCount) { // first page

                res.setPrev(null);
                res.setNext(Resource.fromHref("offset=" + next + "&limit="
                        + limit));
                res.setLast(Resource.fromHref("offset=" + last + "&limit="
                        + limit));
                res.setFirst(Resource.fromHref("offset=" + first + "&limit="
                        + limit));
            } else if (offset != 0 && limit < totalCount) { // medium pages

                res.setPrev(Resource.fromHref("offset=" + previous + "&limit="
                        + limit));
                res.setNext(Resource.fromHref("offset=" + next + "&limit="
                        + limit));
                res.setLast(Resource.fromHref("offset=" + last + "&limit="
                        + limit));
                res.setFirst(Resource.fromHref("offset=" + first + "&limit="
                        + limit));
            }
        }

        res.setHref("offset=" + offset + "&limit=" + limit);
        res.setOffset(offset);
        res.setLimit(limit);
        res.setTotalCount(totalCount);
    }

    public int getLimit() {
        return limit;
    }

    public long getOffset() {
        return offset;
    }

}
