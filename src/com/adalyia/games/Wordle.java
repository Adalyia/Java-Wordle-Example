/**
 * Name: Java-Wordle-Example
 * Author: Adalyia
 * Date: 2022-10-06
 * Description: This is an example of how to create a game class for Wordle in Java without creating custom data types.
 */
package com.adalyia.games;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Wordle
{
    // Color Codes
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_WHITE = "\u001B[37m";

    // Game Settings & Objects
    private static final int WORD_LENGTH = 5;  // Word length, this can be changed if you wish the game to be scaled
    private static final int MAX_GUESSES = 5;  // Max allowed guesses
    private static final Scanner KEYBOARD_IN = new Scanner(System.in); // Only used if the play() method is called

    // Game Instance Variables
    private final String[][][] board = new String[MAX_GUESSES][WORD_LENGTH][2];  // Our board
    private List<String> answersList;  // All valid possible answers in Wordle
    private List<String> dictionaryList;  // All valid possible words in Wordle
    private String answer;  // The answer for the current game instance
    private int guessesMade = 0;  // Count for attempts/guesses made
    private boolean started = false;  // Flag for whether the game has begun
    private String currentGuess = "";  // The current / last guess made

    // Constructors
    public Wordle()
    {
        this.loadWords();
        this.setAnswer(this.getRandomAnswer());
    }

    /**
     * This constructor is used to set the answer for the game instance
     * @param answer The answer to set
     */
    public Wordle(String answer)
    {
        this.loadWords();
        this.setAnswer(answer.toLowerCase());
    }

    // Getters & Setters

    /**
     * Gets the answer for the current Wordle instance
     *
     * @return The current game's answer
     */
    public String getAnswer()
    {
        return this.answer;
    }

    /**
     * Set the answer for this game of Wordle
     *
     * @param answer The answer (Must be 5 characters long)
     * @throws IllegalArgumentException if the answer is not a valid word
     * @throws IllegalStateException    if the game has already started
     */
    public void setAnswer(String answer)
    {
        if (!this.validateWord(answer))
        {
            throw new IllegalArgumentException("Answer must be a valid word in the Wordle dictionary");
        }
        if (this.isStarted())
        {
            throw new IllegalStateException("Game has already started.");
        }
        this.answer = answer;
    }

    /**
     * Gets the board for the current Wordle instance
     * @return The current game's board
     */
    public String[][][] getBoard()
    {
        return board;
    }

    /**
     * Gets the last guess made in the current Wordle instance
     * @return The current game's last guess
     */
    public String getLastGuess()
    {
        return this.currentGuess;
    }

    /**
     * Gets the number of guesses made in the current Wordle instance
     * @return The current game's number of guesses made
     */
    public int getGuessesMade()
    {
        return this.guessesMade;
    }

    /**
     * Gets the maximum number of guesses allowed in the current Wordle instance
     * @return The current game's maximum number of guesses allowed
     */
    public int getMaxGuesses()
    {
        return MAX_GUESSES;
    }

    /**
     * Gets the number of remaining guesses in the game
     * @return The current game's number of remaining guesses
     */
    public int getRemainingGuesses()
    {
        return this.getMaxGuesses() - this.getGuessesMade();
    }

    /**
     * Gets the word length for the current Wordle instance
     * @return The current game's word length
     */
    public int getWordLength()
    {
        return WORD_LENGTH;
    }

    /**
     * Gets whether the current Wordle instance has started
     * @return The current game's started flag
     */
    public boolean isStarted()
    {
        return this.started;
    }
    
    /**
     * Gets the current game's answer list
     * @return The current game's answer list
     */
    public List<String> getAnswersList()
    {
        return this.answersList;
    }

    /**
     * Sets the current game's answer list
     * @throws IllegalStateException if the game has already started
     */
    public void setAnswersList(List<String> answersList)
    {
        if (this.isStarted())
        {
            throw new IllegalStateException("Game has already started.");
        }
        this.answersList = answersList;
    }

    /**
     * Gets the current game's dictionary list
     * @return The current game's dictionary list
     */
    public List<String> getDictionaryList()
    {
        return this.dictionaryList;
    }

    /**
     * Sets the current game's dictionary list
     * @throws IllegalStateException if the game has already started
     */
    public void setDictionaryList(List<String> dictionaryList)
    {
        if (this.isStarted())
        {
            throw new IllegalStateException("Game has already started.");
        }
        this.dictionaryList = dictionaryList;
        this.dictionaryList.addAll(this.getAnswersList());
    }

    /**
     * Checks whether the user won the game
     * @return Whether the user won the game
     */
    public boolean isWinner()
    {
        return this.getLastGuess().equalsIgnoreCase(this.getAnswer());
    }

    /**
     * Checks whether the game is complete or not
     * @return Whether the game is complete or not
     */
    public boolean isComplete()
    {
        return this.getGuessesMade() >= this.getMaxGuesses() || this.isWinner();
    }

    // Core Game Methods

    /**
     * Load the answer and dictionary words
     */
    private void loadWords()
    {
        try
        {
            // Load the list of possible answers
            this.setAnswersList(Files.readAllLines(Path.of(Objects.requireNonNull(this.getClass().getResource("answers_list.txt")).toURI())));
            System.out.printf("Loaded %d potential answers from disk%n", this.getAnswersList().size());
        }
        catch (NullPointerException | IOException | URISyntaxException ex)
        {
            System.out.println("Could not find answers file, does it exist?");
            System.exit(0);
        }
        try
        {
            // Load the list of dictionary words
            this.setDictionaryList(Files.readAllLines(Path.of(Objects.requireNonNull(this.getClass().getResource("dictionary_list.txt")).toURI())));
            System.out.printf("Loaded %d dictionary words from disk%n", this.getDictionaryList().size());
        }
        catch (NullPointerException | IOException | URISyntaxException ex)
        {
            System.out.println("Could not find answers file, does it exist?");
            System.exit(0);
        }

    }

    /**
     * Get a random answer from the list of answers
     *
     * @return The random answer
     */
    private String getRandomAnswer()
    {
        Random random = new Random();
        return this.getAnswersList().get(random.nextInt(this.getAnswersList().size()));
    }

    /**
     * Checks if a word is in the allowed words dictionary
     *
     * @param word The guess to check
     * @return True if the guess is in the dictionary, false otherwise
     */
    private boolean validateWord(String word)
    {
        return this.getDictionaryList().contains(word.toLowerCase());
    }

    /**
     * Color a guess based on the answer
     *
     * @param guess The guess to color
     * @return The colorized guess
     */
    private String[][] gradeGuess(String guess)
    {
        // Temporary vars needed to grade a guess
        HashMap<Character, Integer> answerLetters = new HashMap<>();  // Map of <Letter, Num Of Occurrences>
        String[][] gradedGuess = new String[this.getWordLength()][2];  // The returned graded guess
        char guessLetter;  // Storage for the current character in the guess
        char answerLetter;  // Storage for the current character in the answer

        // Populate the answerLetters map with each letter in the answer and the number of times it appears
        for (char c : this.getAnswer().toCharArray())
        {
            if (answerLetters.containsKey(c))
            {
                answerLetters.put(c, answerLetters.get(c) + 1);
            }
            else
            {
                answerLetters.put(c, 1);
            }
        }

        // Do an initial pass where we grade correct letters in the correct pos and decrement the answerLetters map
        for (int i = 0; i < this.getWordLength(); i++)
        {
            guessLetter = guess.charAt(i);
            answerLetter = this.getAnswer().charAt(i);
            if (answerLetters.containsKey(guessLetter) && answerLetters.get(guessLetter) > 0 && guessLetter == answerLetter)
            {
                gradedGuess[i][0] = String.valueOf(guessLetter);
                gradedGuess[i][1] = ANSI_GREEN;
                answerLetters.put(guessLetter, answerLetters.get(guessLetter) - 1);
            }

        }

        // Do a second pass where we grade correct letters in the wrong pos and decrement the answerLetters map
        for (int i = 0; i < this.getWordLength(); i++)
        {
            guessLetter = guess.charAt(i);
            answerLetter = this.getAnswer().charAt(i);
            if (answerLetters.containsKey(guessLetter) && answerLetters.get(guessLetter) > 0 && guessLetter != answerLetter)
            {
                gradedGuess[i][0] = String.valueOf(guessLetter);
                gradedGuess[i][1] = ANSI_YELLOW;
                answerLetters.put(guessLetter, answerLetters.get(guessLetter) - 1);
            }
            else if (gradedGuess[i][0] == null)
            {
                // If the letter is not in the answer, color it white/neutral
                gradedGuess[i][0] = String.valueOf(guessLetter);
                gradedGuess[i][1] = ANSI_WHITE;
            }

        }

        return gradedGuess;
    }

    /**
     * Add a guess to the board
     *
     * @param guess The guess to add
     */
    public void addGuess(String guess)
    {
        // Change the game state to started
        this.started = true;

        // Guard clauses for invalid words or a bad game state
        if (this.isComplete())
        {
            throw new IllegalStateException("Game has already ended.");
        }
        if (!this.validateWord(guess))
        {
            throw new IllegalArgumentException("Guess must be a valid 5-letter word");
        }

        // Change the last guess to the current guess as it's been validated
        this.currentGuess = guess;

        // Add the guess to the board
        this.board[this.guessesMade] = this.gradeGuess(guess);

        // Increment the number of guesses made
        this.guessesMade++;
    }

    // Methods for playing a Wordle game in the console

    /**
     * Colorize a string
     *
     * @param str   The string to colorize
     * @param color The color to use
     * @return The colorized string
     */
    private String colorize(String str, String color)
    {
        return color + str + ANSI_RESET;
    }

    /**
     * Prompt the user for a guess
     *
     * @return The user's guess
     */
    private String guessPrompt()
    {
        // Prompt the user for a guess
        System.out.print("Enter your guess: ");
        String guess = KEYBOARD_IN.nextLine();

        // Validate the guess, this is a little redundant, but it's a good way to keep the game loop clean
        if (this.validateWord(guess))
        {
            return guess;
        }
        else
        {
            // Recursively call the prompt if the guess is invalid
            System.out.println("Invalid guess, try again!");
            return this.guessPrompt();
        }
    }

    /**
     * Print the game bord in a readable format
     */
    private void printBoard()
    {
        // If there's 0 guesses or attempts made there's nothing to print
        if (this.getGuessesMade() == 0)
        {
            return;
        }

        // Draw the upper bound of the game board
        System.out.printf("+%s+%n", "-".repeat(this.getWordLength()));

        for (int i = 0; i < this.getGuessesMade(); i++)
        {
            // Draw the left bound of the game board
            System.out.print("|");

            for (String[] letter : this.board[i])
            {
                // Print the colorized letter according to how it was graded
                System.out.printf("%s", this.colorize(
                        letter[0].toUpperCase(), // The board letters should be upper case
                        letter[1]
                ));
            }

            // Draw the right bound of the game board
            System.out.println("|");
        }

        // Draw the lower bound of the game board
        System.out.printf("+%s+%n", "-".repeat(this.getWordLength()));

        // Show stats/current information
        System.out.printf("Guesses made: %d%n", this.getGuessesMade());
        System.out.printf("Guesses left: %d%n", this.getRemainingGuesses());

    }

    /**
     * Play a game of Wordle in the console
     * Warning: Some may not support ANSI escape codes for color
     */
    public void play()
    {
        // Change the game state to started
        this.started = true;
        
        // Print the introduction
        System.out.println("Welcome to Wordle!");
        System.out.println("You have 5 guesses to guess the 5 letter word.");
        System.out.printf("%s letters are in the word at the correct position%n", colorize("Green", ANSI_GREEN));
        System.out.printf("%s letters are in the word at the wrong position%n", colorize("Yellow", ANSI_YELLOW));
        System.out.printf("%s letters are not in the word at all %n", colorize("White", ANSI_WHITE));

        // Our game loop, runs until the user guesses the word or runs out of guesses
        while (!this.isComplete())
        {
            // Print the board
            this.printBoard();

            // Prompt the user for a guess and attempt to add it to the board
            this.addGuess(this.guessPrompt());
        }

        // Print the final board
        this.printBoard();

        // Check if the user won or lost
        if (this.isWinner())
        {
            System.out.println("You win!");
        }
        else
        {
            // Show the correct answer if they lost
            System.out.printf("You lose! The answer was: %s%n", this.getAnswer().toUpperCase());
        }

    }

    // Main method for running the game
    /**
     * Start the game in the console
     */
    public static void main(String[] args)
    {
        Wordle wordle = new Wordle();
        wordle.play();
    }
}
