package com.portfolio.GuessNumber.controller;

import jakarta.servlet.http.HttpSession;
import java.util.Random;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/guess-number")
public class GameController {

    @GetMapping()
    public String getHome(){
        return "home";
    }

    @GetMapping("/game1")
    public String getGame1(HttpSession session){
        if (session.getAttribute("computerThoughtNumber") == null) {
            Random random = new Random();
            int computerThoughtNumber = random.nextInt(11);
            session.setAttribute("computerThoughtNumber", computerThoughtNumber);
            session.setAttribute("userGuesses", 0);
        }
        return "game";
    }

    @PostMapping("/process-game1")
    public String postProcessGame1(@RequestParam("userGuess") int userGuess,
                                  HttpSession session, Model model){

        Integer computerThoughtNumber = (Integer) session.getAttribute("computerThoughtNumber");
        Integer userGuesses = (Integer) session.getAttribute("userGuesses");

        Random random = new Random();

        if (computerThoughtNumber == null) {
            computerThoughtNumber = random.nextInt(11);
            session.setAttribute("computerThoughtNumber", computerThoughtNumber);
        }
        if (userGuesses == null) {
            userGuesses = 0;
            session.setAttribute("userGuesses", userGuesses);
        }

        String message;

        if (userGuess == computerThoughtNumber){
            message = "Congrats! You win.";
            session.setAttribute("userGuesses", userGuesses + 1);
            model.addAttribute(message, message);
            return "redirect:/guess-number/game2";
        }else if (userGuess < 0){
            message = "Please enter a valid positive number!";
            model.addAttribute(message, message);
        }else if (userGuess > computerThoughtNumber){
            message = "Try lower numbers!";
            session.setAttribute("userGuesses", userGuesses + 1);
            model.addAttribute(message, message);
        }else if (userGuess < computerThoughtNumber ) {
            message = "Try higher numbers!";
            model.addAttribute(message, message);
            session.setAttribute("userGuesses", userGuesses + 1);
        }else {
            message = "Number should be between 0 to 10.";
            model.addAttribute(message, message);
        }

        model.addAttribute("message", message);

        return "game";
    }


    @GetMapping("/game2")
    public String getGame2(HttpSession session, Model model){

        Random random = new Random();

        if (session.getAttribute("computerGuess") == null) {
            int computerGuess = random.nextInt(11);
            session.setAttribute("computerGuess", computerGuess);
            session.setAttribute("computerGuesses", 0);
        }

        Integer computerGuess = (Integer)session.getAttribute("computerGuess");
        model.addAttribute("computerGuess", computerGuess);

        return "game2";
    }

    @PostMapping("/process-game2")
    public String postProcessGame2(@RequestParam("feedback") String userFeedback,
                                   HttpSession session, Model model){
        Integer computerGuess = (Integer) session.getAttribute("computerGuess");
        Integer computerGuesses = (Integer) session.getAttribute("computerGuesses");
        String message = null;

        Random random = new Random();

        model.addAttribute("computerGuess", computerGuess);

        // If attributes are still null, initialize them
        if (computerGuess == null) {
            computerGuess = random.nextInt(11);
            session.setAttribute("computerGuess", computerGuess);
        }
        if (computerGuesses == null) {
            computerGuesses = 0;
            session.setAttribute("computerGuesses", computerGuesses);
        }


        if (userFeedback.equals("c")){
            message = "Oh got it. That was a good try.";
            computerGuesses += 1;
            session.setAttribute("computerGuesses", computerGuesses);

            Integer userGuesses = (Integer) session.getAttribute("userGuesses");

            if (userGuesses < computerGuesses){
                session.setAttribute("winner", "user");
            }else if(userGuesses > computerGuesses){
                session.setAttribute("winner", "computer");
            }else {
                session.setAttribute("winner", "both");
            }

            return "redirect:/guess-number/result";
        } else if (userFeedback.equals("h")){
            computerGuess -= 1;
            computerGuesses += 1;
            session.setAttribute("computerGuesses", computerGuesses);
        } else if (userFeedback.equals("s")){
            computerGuess += 1;
            computerGuesses += 1;
            session.setAttribute("computerGuesses", computerGuesses);
        } else {
            message = "Invalid input. Please enter 'c', 'h', or 's'.";
        }

        session.setAttribute("computerGuesses", computerGuesses);
        session.setAttribute("computerGuess", computerGuess);
        model.addAttribute("computerGuess", computerGuess);
        model.addAttribute("message", message);

        return "game2";
    }

    @GetMapping("/result")
    public String getResult(HttpSession session, Model model){
        String winner = (String) session.getAttribute("winner");
        Integer computerGuesses = (Integer) session.getAttribute("computerGuesses");
        Integer userGuesses = (Integer) session.getAttribute("userGuesses");

        if (userGuesses == null) userGuesses = 0;
        if (computerGuesses == null) computerGuesses = 0;

        session.setAttribute("userGuesses", userGuesses);
        session.setAttribute("computerGuesses", computerGuesses);
        
        if (winner.equals("user")){
            model.addAttribute("winner", "user");
            model.addAttribute("totalGuesses", userGuesses);
        }else if (winner.equals("computer")){
            model.addAttribute("winner", "computer");
            model.addAttribute("totalGuesses", computerGuesses);
        }else {
            model.addAttribute("winner", "both");
            model.addAttribute("totalGuesses", computerGuesses);
        }

        session.invalidate();

        return "result";
    }
}
