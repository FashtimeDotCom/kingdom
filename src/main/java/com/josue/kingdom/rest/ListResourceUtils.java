/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.rest;

import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.PaginationUtils;
import com.josue.kingdom.rest.Resource;
import java.util.List;

/**
 *
 * @author Josue
 */
public class ListResourceUtils {

    private ListResourceUtils() {

    }

    public static <T extends Resource> ListResource<T> buildListResource(List<T> resources, long totalCount, Integer limit, Integer offset) {
        ListResource<T> listResource = new ListResource<>(resources);
        PaginationUtils paginationUtils = new PaginationUtils(limit, offset);
        paginationUtils.fillPagination(listResource, totalCount);
        return listResource;
    }

}
