package com.sebit.movie.Repository;

import com.sebit.movie.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByDirectorContainingIgnoreCase(String director);

    List<Movie> findByYear(String year);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(:keyword)")
    List<Movie> searchByTitle(@Param("keyword") String keyword);

    @Query("select m from Movie m where lower(m.title) like lower(concat('%', :title, '%')) and m.year like :year")
    List<Movie> findByTitleContainingIgnoreCaseAndYear(String title, String year);

}