package com.sebit.movie.Model;
import lombok.Data;

@Data
public class MovieRequest {
    private String imdbId;
    private String title;
    private String year;
    private String type;
}
