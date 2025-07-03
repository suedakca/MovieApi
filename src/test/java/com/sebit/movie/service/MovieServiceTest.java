package com.sebit.movie.service;

import com.sebit.movie.Controller.MovieGraphQLController;
import com.sebit.movie.Entity.Movie;
import com.sebit.movie.Model.MovieRequest;
import com.sebit.movie.Repository.MovieRepository;
import com.sebit.movie.Service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieGraphQLController omdbApiClient;

    @InjectMocks
    private MovieService movieService;

    private MovieRequest movieRequest;

    @BeforeEach
    void setUp() {
        movieRequest = new MovieRequest();
        movieRequest.setImdbId("tt1234567");
        movieRequest.setTitle("Inception");
        movieRequest.setYear("2010");
        movieRequest.setType("movie");
    }

    @Test
    void shouldReturnExistingMoviesIfFoundAndImportedIsNull() {
        Movie existingMovie = new Movie();
        existingMovie.setTitle("Inception");
        existingMovie.setYear("2010");

        when(movieRepository.findByTitleContainingIgnoreCaseAndYear("Inception", "2010"))
                .thenReturn(List.of(existingMovie));

        when(omdbApiClient.importMovieAuto("tt1234567", "Inception", "2010", "movie"))
                .thenReturn(null);

        List<Movie> result = movieService.searchAndSaveMovies(movieRequest);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
        verify(movieRepository).findByTitleContainingIgnoreCaseAndYear("Inception", "2010");
        verify(omdbApiClient).importMovieAuto("tt1234567", "Inception", "2010", "movie");
    }

    @Test
    void shouldReturnImportedMovieIfNotExistsInDb() {
        Movie importedMovie = new Movie();
        importedMovie.setTitle("Interstellar");
        importedMovie.setYear("2014");

        when(movieRepository.findByTitleContainingIgnoreCaseAndYear("Inception", "2010"))
                .thenReturn(List.of());

        when(omdbApiClient.importMovieAuto("tt1234567", "Inception", "2010", "movie"))
                .thenReturn(importedMovie);

        List<Movie> result = movieService.searchAndSaveMovies(movieRequest);

        assertEquals(1, result.size());
        assertEquals("Interstellar", result.get(0).getTitle());
        verify(movieRepository).findByTitleContainingIgnoreCaseAndYear("Inception", "2010");
        verify(omdbApiClient).importMovieAuto("tt1234567", "Inception", "2010", "movie");
    }

    @Test
    void shouldSearchWithoutYearIfYearIsNull() {
        movieRequest.setYear(null);

        Movie importedMovie = new Movie();
        importedMovie.setTitle("Matrix");

        when(movieRepository.findByTitleContainingIgnoreCase("Inception"))
                .thenReturn(List.of());

        when(omdbApiClient.importMovieAuto("tt1234567", "Inception", null, "movie"))
                .thenReturn(importedMovie);

        List<Movie> result = movieService.searchAndSaveMovies(movieRequest);

        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).getTitle());
        verify(movieRepository).findByTitleContainingIgnoreCase("Inception");
    }
}