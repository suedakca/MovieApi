package com.sebit.movie.Controller;

import com.sebit.movie.Entity.Movie;
import com.sebit.movie.Model.MovieResponse;
import com.sebit.movie.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/import")
public class MovieCtrl {
    @Value("${omdb.api.key}")
    private String apiKey;
    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;

    public MovieCtrl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/{imdbID}")
    public Movie importMovie(@PathVariable String imdbID) {
        String url = "https://www.omdbapi.com/?apikey="+ apiKey + "&i=" + imdbID;

        MovieResponse omdbMovie = restTemplate.getForObject(url, MovieResponse.class);

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