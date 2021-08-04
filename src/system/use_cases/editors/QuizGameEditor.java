package system.use_cases.editors;

import system.entities.game.quiz.QuizGame;
public class QuizGameEditor {
    public QuizGameEditor() {
    }

    public QuizGame editQuizGame(QuizGame oldGame, QuizGame newGame) {
        newGame.setID(oldGame.getID());
        return newGame;
    }

}
