package com.example.game_service_api.services;

import com.example.game_service_api.commons.entities.Game;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface GameService {
    Game saveGame(Game gameRequest);
    Game getGameById(String id);
    List<Game> getAllGames();
    void deleteGameById(Long id);
    Game putGameById(Long id, Game gameRequest);
}
