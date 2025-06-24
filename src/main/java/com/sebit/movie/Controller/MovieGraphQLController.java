package com.sebit.movie.Controller;

import com.sebit.movie.Entity.Movie;
import com.sebit.movie.Model.MovieResponse;
import com.sebit.movie.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;
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

    @QueryMapping
    public List<Movie> movies() {
        return movieRepository.findAll();
    }

    @QueryMapping
    public Movie movieById(@Argument Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    @MutationMapping
    public Movie importMovie(@Argument String imdbID) {
        String url = "https://www.omdbapi.com/?apikey="+ apiKey +"&i=" + imdbID;
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