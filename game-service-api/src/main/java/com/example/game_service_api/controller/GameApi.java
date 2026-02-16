package com.example.game_service_api.controller;

import com.example.game_service_api.commons.constants.ApiPathVariables;
import com.example.game_service_api.commons.entities.Game;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController (ApiPathVariables.V1_ROUTE+ApiPathVariables.GAME_ROUTE)
public interface GameApi {
    @PostMapping
    ResponseEntity<Game> saveGame(@RequestHeader String userId, @RequestBody Game game);
    @GetMapping("/{id}")
    ResponseEntity<Game> getGameById(@PathVariable String id);
    @GetMapping
    ResponseEntity<List<Game>> getAllGames();
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteGame(@PathVariable Long id);
    @PutMapping("/{id}")
    ResponseEntity<Game> putGame(@PathVariable Long id,@RequestBody Game gameRequest);

}
