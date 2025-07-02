package com.sebit.movie.Service;

import com.sebit.movie.Controller.MovieGraphQLController;
import com.sebit.movie.Entity.Movie;
import com.sebit.movie.Model.MovieRequest;
import com.sebit.movie.Repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieGraphQLController omdbApiClient;

    public MovieService(MovieRepository movieRepository, MovieGraphQLController omdbApiClient) {
        this.movieRepository = movieRepository;
        this.omdbApiClient = omdbApiClient;
    }

    public List<Movie> searchAndSaveMovies(MovieRequest request) {
        Movie importedMovie = omdbApiClient.importMovieAuto(
                request.getImdbId(),
                request.getTitle(),
                request.getYear(),
                request.getType()
        );

        List<Movie> existingMovies;
        if(request.getYear() != null) {
            existingMovies = movieRepository.findByTitleContainingIgnoreCaseAndYear(request.getTitle(), request.getYear());
        } else {
            existingMovies = movieRepository.findByTitleContainingIgnoreCase(request.getTitle());
        }
        if (!existingMovies.isEmpty() && importedMovie == null) {
            return existingMovies;
        }

        return List.of(importedMovie);
    }
}
