package com.sebit.movie.Controller;

import com.sebit.movie.Entity.Movie;
import com.sebit.movie.Model.MovieRequest;
import com.sebit.movie.Repository.MovieRepository;
import com.sebit.movie.Service.MovieService;
import com.sebit.movie.Util.ExcelCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MovieCtrl {
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    @GetMapping("/all")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @PostMapping("/search-movie")
    public ResponseEntity<List<Movie>> searchMovie(@RequestBody MovieRequest movieRequest) {
        List<Movie> movies = movieService.searchAndSaveMovies(movieRequest);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/report-download")
    public ResponseEntity<ByteArrayResource> downloadMovieReport() {
        List<Movie> movies = movieRepository.findAll();
        ByteArrayResource resource = new ByteArrayResource(ExcelCreator.generateExcelReport(movies));
        try {
            // Widows -> C:/Users/sueda_akca/Desktop/homeworks.xlsx
            // Mac -> /Users/suedaakca/Desktop/homeworks.xlsx
            Files.write(Paths.get("C:/Users/sueda_akca/Desktop/movie.xlsx"), resource.getByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movie.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

}
