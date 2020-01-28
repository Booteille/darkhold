package com.quiz.darkhold.preview.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.entity.GameStatus;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.model.PreviewInfo;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PreviewService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CurrentGame currentGame;

    public PreviewInfo fetchQuestions(String challengeId) {
        PreviewInfo previewInfo = new PreviewInfo();
        Long challengeOne = Long.valueOf(challengeId);
        Challenge challenge = challengeRepository.getOne(challengeOne);
        previewInfo.setQuestionSets(new ArrayList<>(challenge.getQuestionSets()));
        previewInfo.setChallengeName(challenge.getTitle());
        previewInfo.setChallengeId(challengeId);
        return previewInfo;
    }

    public PublishInfo generateQuizPin(String challengeId) {
        String generatedString = RandomStringUtils.random(5, false, true);
        //todo : link challenge to game
        String currentUser = getUsername();
        Game game = new Game();
        game.setPin(generatedString);
        game.setGameStatus(GameStatus.WAITING.name());
        game.setChallengeId(challengeId);
        gameRepository.save(game);
        PublishInfo publishInfo = new PublishInfo();
        publishInfo.setPin(generatedString);
        publishInfo.setUsername(currentUser);
        currentGame.saveCurrentStatus(publishInfo);
        return publishInfo;
    }

    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        return user.getUsername();
    }

    public PublishInfo getActiveChallenge() {
        List<Game> activeGames = gameRepository.findByGameStatusNot(GameStatus.FINISHED.name());
        Game activeGame = activeGames.get(0);
        //currently, we are taking the first one, may need optimization if we run multiple games in parallel
        PublishInfo publishInfo = new PublishInfo();
        publishInfo.setPin(activeGame.getPin());
        publishInfo.setUsername(getUsername());
        return publishInfo;
    }
}
