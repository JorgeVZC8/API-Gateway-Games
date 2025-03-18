package com.example.game_service_api.services.impl;

import com.example.game_service_api.commons.entities.Game;
import com.example.game_service_api.commons.exceptions.GameException;
import com.example.game_service_api.repositories.GameRepository;
import com.example.game_service_api.services.GameService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game saveGame(Game gameRequest){
        if (this.gameRepository.existsById(gameRequest.getId())){
            throw new GameException("There is already a game with this ID on the database", HttpStatus.BAD_REQUEST);
        }
        return this.gameRepository.save(gameRequest);
    }

    @Override
    public Game getGameById(String id) {
        return this.gameRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new GameException("Error finding game", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Game> getAllGames() {
        return this.gameRepository.findAll();
    }

    @Override
    public void deleteGameById(Long id) {
        if (!this.gameRepository.existsById(id)) {
            throw new GameException("The game does not exist", HttpStatus.NOT_FOUND);
        }
        this.gameRepository.deleteById(id);
    }

    @Override
    public Game putGameById(Long id, Game gameRequest) {
        return gameRepository.findById(id)
                .map(game ->{
                    game.setName(gameRequest.getName());
                    return gameRepository.save(game);
                })
                .orElseThrow(() -> new GameException("Game not found", HttpStatus.NOT_FOUND));
    }


}