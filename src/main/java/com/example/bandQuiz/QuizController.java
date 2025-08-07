package com.example.bandQuiz;

import com.example.bandQuiz.quiz.QuizInitializer;
import com.example.bandQuiz.quiz.QuizQuestion;
import com.example.bandQuiz.songsData.ExceptionDuringDataSongFetch;
import com.example.bandQuiz.songsData.InterpretNotFound;
import com.example.bandQuiz.songsData.NotEnoughLyrics;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class QuizController {
    private final QuizInitializer initializer;

    public QuizController(QuizInitializer searchService) {
        this.initializer = searchService;
    }

    @GetMapping("/search-interpret")
    public String search(@RequestParam("q") String interpretName, Model model, HttpSession session)
            throws ExceptionDuringDataSongFetch, InterpretNotFound, NotEnoughLyrics {
        if (interpretName == null || interpretName.trim().isEmpty()) {
            model.addAttribute("error", "Search term must not be empty.");
            return "fragments/error-message :: errorText";
        }

        try {
            List<QuizQuestion> questions = initializer.generateQuiz(interpretName);
            session.setAttribute("quizQuestions", questions); // Store in session
            model.addAttribute("questions", questions);
            return "fragments/quiz-form :: quizForm";
        } catch (InterpretNotFound e) {
            model.addAttribute("error", "Artist not found, check spelling or try another interpret.");
            return "fragments/error-message :: errorText";
        } catch (NotEnoughLyrics e) {
            model.addAttribute("error", "Not enough songs with lyrics found for the artist.");
            return "fragments/error-message :: errorText";
        } catch (ExceptionDuringDataSongFetch e) {
            model.addAttribute("error", "Something went wrong. Try again later.");
            return "fragments/error-message :: errorText";
        }
    }

    @PostMapping("/submit-quiz")
    public String submitQuiz(@RequestBody Map<String, String> answers, Model model, HttpSession session) {
        List<QuizQuestion> questions = (List<QuizQuestion>) session.getAttribute("quizQuestions");
        if (questions == null) {
            model.addAttribute("error", "Quiz session expired. Please start a new quiz.");
            return "fragments/error-message :: errorText";
        }

        int score = initializer.checkAnswers(questions, answers);
        model.addAttribute("score", score);
        model.addAttribute("total", questions.size());
        //model.addAttribute("questions", questions);
        return "fragments/quiz-result :: quizResults";
    }

    @GetMapping("/new-quiz")
    public String getNewQuiz(Model model) throws ExceptionDuringDataSongFetch, InterpretNotFound {
        return "fragments/search-area :: search-area";
    }
}