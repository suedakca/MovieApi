package com.sebit.movie.Repository;

import com.sebit.movie.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Optional: Custom query'ler (istenirse kullanırsın)

    List<Movie> findByDirectorContainingIgnoreCase(String director);

    List<Movie> findByYear(String year);

    List<Movie> findByTitleContainingIgnoreCase(String title);

}