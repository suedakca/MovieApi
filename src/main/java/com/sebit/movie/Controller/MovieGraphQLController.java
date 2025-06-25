package com.sebit.movie.Controller;

import com.sebit.movie.Entity.Movie;
import com.sebit.movie.Model.MovieResponse;
import com.sebit.movie.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class MovieGraphQLController {
    @Value("${omdb.api.key}")
    private String apiKey;
    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;

    public MovieGraphQLController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        this.restTemplate = new RestTemplate();
    }

    @MutationMapping
    public Movie importMovieAuto(
            @Argument String imdbID,
            @Argument String title,
            @Argument String year,
            @Argument String type
    ) {
        String url;
        if (imdbID != null && !imdbID.isEmpty()) {
            url = "https://www.omdbapi.com/?apikey=" + apiKey + "&i=" + imdbID;
        } else if (title != null && !title.isEmpty()) {
            url = "https://www.omdbapi.com/?apikey=" + apiKey + "&t=" + title;

            if (year != null && !year.isEmpty()) {
                url += "&y=" + year;
            }

            if (type != null && !type.isEmpty()) {
                url += "&type=" + type;
            }
        } else {
            throw new IllegalArgumentException("Invalid parameters");
        }


        MovieResponse omdbMovie = restTemplate.getForObject(url, MovieResponse.class);

        if (omdbMovie == null || !"True".equalsIgnoreCase(omdbMovie.getResponse())) {
            throw new RuntimeException("OMDb API Error: " + (omdbMovie != null ? omdbMovie.getError() : "Unknown error"));
        }

        List<Movie> existingMovie = movieRepository.searchByTitle(omdbMovie.getTitle());
        if (existingMovie != null && !existingMovie.isEmpty()) {
            return null;
        }

        Movie movie = new Movie();
        movie.setImdbId(omdbMovie.getImdbID());
        movie.setTitle(omdbMovie.getTitle());
        movie.setYear(omdbMovie.getYear());
        movie.setRated(omdbMovie.getRated());
        movie.setReleased(omdbMovie.getReleased());
        movie.setRuntime(omdbMovie.getRuntime());
        movie.setGenre(omdbMovie.getGenre());
        movie.setDirector(omdbMovie.getDirector());
        movie.setWriter(omdbMovie.getWriter());
        movie.setActors(omdbMovie.getActors());
        movie.setPlot(omdbMovie.getPlot());
        movie.setLanguage(omdbMovie.getLanguage());
        movie.setCountry(omdbMovie.getCountry());
        movie.setAwards(omdbMovie.getAwards());
        movie.setPoster(omdbMovie.getPoster());
        movie.setMetascore(omdbMovie.getMetascore());
        movie.setImdbRating(omdbMovie.getImdbRating());
        movie.setImdbVotes(omdbMovie.getImdbVotes());
        movie.setType(omdbMovie.getType());
        movie.setBoxOffice(omdbMovie.getBoxOffice());

        return movieRepository.save(movie);
    }

}