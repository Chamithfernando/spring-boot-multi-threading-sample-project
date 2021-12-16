package com.example.executor.api.controller;

import com.example.executor.api.entity.Movie;
import com.example.executor.api.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping(value = "/movies", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity saveMovies(@RequestParam(value = "files")MultipartFile[] files) throws Exception {
        for (MultipartFile file : files){
            movieService.saveMovies(file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/movies", produces = "application/json")
    public CompletableFuture<ResponseEntity> findAllMovies(){
       return movieService.findAllMovies().thenApply(ResponseEntity :: ok);
    }

    @GetMapping(value = "/moviesByMultipleThreads", produces = "application/json")
    public ResponseEntity getUsers(){
        CompletableFuture<List<Movie>> movie1 = movieService.findAllMovies();
        CompletableFuture<List<Movie>> movie2 = movieService.findAllMovies();
        CompletableFuture<List<Movie>> movie3 = movieService.findAllMovies();
        CompletableFuture<List<Movie>> movie4 = movieService.findAllMovies();
        CompletableFuture.allOf(movie1,movie2,movie3,movie4).join();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
