package com.socatel.controllers;

import com.socatel.components.Methods;
import com.socatel.models.Answer;
import com.socatel.models.Feedback;
import com.socatel.models.Question;
import com.socatel.models.User;
import com.socatel.services.answer.AnswerService;
import com.socatel.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class QuestionAndAnswerController {

    private final AnswerService answerService;
    private final UserService userService;

    public QuestionAndAnswerController(AnswerService answerService, UserService userService) {
        this.answerService = answerService;
        this.userService = userService;
    }

    @Transactional
    @RequestMapping(value = "/answer/{answer_id}/vote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity voteAnswer(@PathVariable(value = "answer_id") Integer answerId) {
        Question q;
        Feedback f = null;
        User user;
        List<Answer> answers;
        List<Integer> votes;
        Answer answer = answerService.findById(answerId);
        user = Methods.getLoggedInUser(userService);
        if (answer == null)
            return ResponseEntity.badRequest().build();
        q = answer.getQuestion();
        if (q == null) {
            // if voted in step 4
            f = answer.getFeedback();
            answers = answerService.findByFeedback(f);
        } else {
            // if voted in step 3
            answers = answerService.findByQuestion(q);
        }
        if (answer.hasVoted(user)) { // if voted, unvote
            answer.removeUser(user);
            user.removeAnswer(answer);
        } else { // vote
            // if user voted another answer in the same question, unvote
            /*
            for (Answer a : answers) {
                if (a.hasVoted(user)) {
                    a.removeUser(user);
                    user.removeAnswer(a);
                }
            }
            */
            // vote corresponding answer
            answer.addUser(user);
            user.addAnswer(answer);
            answer.setVotes(answer.getUsersVotes().size());
        }
        // Return number of votes for ...
        if (q == null) {
            // ... for each feedback
            votes = answerService.findByFeedback(f).stream().map(Answer::getUsersVotes).map(Set::size).collect(Collectors.toList());
        } else {
            // ... for each question
            votes = answerService.findByQuestion(q).stream().map(Answer::getUsersVotes).map(Set::size).collect(Collectors.toList());
        }

        return ResponseEntity.ok(votes);
        //return "redirect:/topic/" + answer.getQuestion().getGroup().getId();
    }
}

/*
            // unvote all answers
        for (Answer a : answers) {
            if (a.hasVoted(user)) {
                a.removeUser(user);
                user.removeAnswer(a);
            }
            a.setVotes(a.getUsersVotes().size());
        }
        // vote corresponding answer
        answer.addUser(user);
        user.addAnswer(answer);
        answer.setVotes(answer.getUsersVotes().size());
        Integer numVotes = answer.getUsersVotes().size();

        return ResponseEntity.ok(numVotes);
     */