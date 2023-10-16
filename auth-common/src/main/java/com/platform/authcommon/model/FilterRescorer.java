package com.platform.authcommon.model;

import org.apache.mahout.cf.taste.recommender.IDRescorer;

import java.util.Set;

public class FilterRescorer implements IDRescorer {

    final private Set<Long> userIds;

    public FilterRescorer(Set<Long> userIds) {
        this.userIds = userIds;
    }

    @Override
    public double rescore(long id, double originalScore) {
        return isFiltered(id) ? Double.NaN : originalScore;
    }

    @Override
    public boolean isFiltered(long id) {
        return userIds.contains(id);
    }
}
