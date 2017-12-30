package com.androiddevelopernanodegree.nahla.popularmoviesstage2.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieTrailersResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<TrailersResult> trailersResults = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TrailersResult> getTrailersResults() {
        return trailersResults;
    }

    public void setTrailersResults(List<TrailersResult> trailersResults) {
        this.trailersResults = trailersResults;
    }
}
