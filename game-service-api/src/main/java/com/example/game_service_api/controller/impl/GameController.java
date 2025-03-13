package com.example.game_service_api.controller.impl;

import com.example.game_service_api.commons.entities.Game;
import com.example.game_service_api.commons.exceptions.GameException;
import com.example.game_service_api.controller.GameApi;
import com.example.game_service_api.services.GameService;
import com.example.game_service_api.services.impl.GameServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController implements GameApi {
    private final GameService gameService;

    public GameController(GameServiceImpl gameService) {
        this.gameService = gameService;
    }

    @Override
    public ResponseEntity<Game> saveGame(@RequestBody Game game){
        var gameCreated = this.gameService.saveGame(game);
        return ResponseEntity.ok(gameCreated);
    }

    @Override
    public ResponseEntity<Game> getGameById(String id){
        return ResponseEntity.ok(this.gameService.getGameById(id));
    }

    @Override
    public ResponseEntity<List<Game>> getAllGames() {
        return ResponseEntity.ok(this.gameService.getAllGames());
    }

    @Override
    public ResponseEntity<String> deleteGame(Long id) {
        try{
            this.gameService.deleteGameById(id);
            return ResponseEntity.ok("The game whose ID is: "+id+ " was deleted successfully");
        }catch(GameException e){
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }

    }

    @Override
    public ResponseEntity<Game> putGame(Long id, Game gameRequest) {
        return ResponseEntity.ok(this.gameService.putGameById(id,gameRequest));
    }


}
