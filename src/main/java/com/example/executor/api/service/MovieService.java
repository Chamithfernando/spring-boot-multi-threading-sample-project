package com.example.executor.api.service;

import com.example.executor.api.entity.Movie;
import com.example.executor.api.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    Object target;
    Logger logger = LoggerFactory.getLogger(MovieService.class);

    @Async
    public CompletableFuture<List<Movie>> saveMovies(MultipartFile file) throws Exception {
        long start = System.currentTimeMillis();
        List<Movie> movies = parseCsv(file);
        logger.info("saving list of movies os size {}",movies.size(),"" + Thread.currentThread().getName());
        movies = movieRepository.saveAll(movies);
        long end = System.currentTimeMillis();
        logger.info("total time {}", (end - start));
        return CompletableFuture.completedFuture(movies);


    }

    @Async
    public CompletableFuture<List<Movie>> findAllMovies(){
        logger.info("get list of movie by "+ Thread.currentThread().getName());
        List<Movie> movies = movieRepository.findAll();
        return CompletableFuture.completedFuture(movies);
    }

    private List<Movie> parseCsv(final MultipartFile file) throws Exception{
        final List<Movie> movies = new ArrayList<>();
        try {
            try(final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))){
                String line;
                while ((line = br.readLine()) != null){
                    final String[] data  = line.split(",");
                    final Movie movie = new Movie();
                    movie.setName(data[0]);
                    movie.setCategory(data[1]);
                    movie.setYear(data[2]);
                    movies.add(movie);
                }
                return movies;
            }
        }catch (final IOException e){
            logger.error("filed to passed csv file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }
}
