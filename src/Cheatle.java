import java.util.*;
import java.io.File;
import java.io.IOException;

public class Cheatle {
	
	private TreeSet<String> solutions = new TreeSet<String>();
	private TreeSet<String> resetSols = new TreeSet<String>();
	private TreeSet<String> words = new TreeSet<String>();
	private String alphabet = "";
	private int wordLength = 0;
	private TreeSet<String> valid = new TreeSet<String>();
	private TreeSet<String> eliminated = new TreeSet<String>();
	private TreeSet<String> goodPos = new TreeSet<String>();
	private TreeSet<String> badPos= new TreeSet<String>();
	private int numGuesses;
	

	//Reads the dictionaryFile and puts all the allowed guesses into a data structure,
	//and also reads the solutionFile and puts all the possible solutions into a data structure,
	//also adding all the possible solutions to the allowed guesses.
	//Throws a BadDictionaryException if not every word in the dictionary & solutions are of the same length 
	public Cheatle(String dictionaryFile, String solutionFile) throws IOException, BadDictionaryException {
		File file = new File(dictionaryFile);
        Scanner sc = new Scanner(file);
        File solFile = new File(solutionFile);
		Scanner s = new Scanner(solFile);
        wordLength = s.next().length();
        boolean isValid = true;
        String word = "";
        
        try
        {
        	while(sc.hasNext() && isValid)
            {
        		word = sc.next();
            	if(wordLength != word.length())
            	{
            		isValid = false;
            	}
                words.add(word);
                words.add(word);
                for(int i = 0; i < word.length(); i++)
                {
                	if(!valid.contains(""+word.charAt(i)))
                	{
                		valid.add(""+word.charAt(i));
                	}
                }
            }
    		
    		while(s.hasNext() && isValid)
    		{
    			word = s.next();
    			if(wordLength != word.length())
            	{
    				isValid = false;
            	}
    			words.add(word);
    			words.add(word);
    			solutions.add(word);
    			solutions.add(word);
    			resetSols.add(word);
    			resetSols.add(word);
    		}
    		sc.close();
    		s.close();
    		for(String str : valid)
    		{
    			alphabet += str;
    		}
    		if(!isValid)
    			throw new BadDictionaryException();

        }
        catch (Exception e)
        {
        	
        }
		
	}
	
	public String scoreGuess(String guess, String solution) {
		ArrayList<String> Guess = new ArrayList<String>();
		ArrayList<String> Solution = new ArrayList<String>();
		String feedback = "";
		int nR = 0;
		
		for(int i = 0; i < guess.length(); i++)
		{
			Guess.add(""+ guess.charAt(i));
			Solution.add(""+ solution.charAt(i));
		}
		 
		char[] score = new char[guess.length()];
		for(int i = 0; i < score.length; i++)
		{
			score[i] = '*';
		}
		
		for(int i = 0; i < guess.length(); i++)
		{
			if(guess.charAt(i) == solution.charAt(i))
			{
				score[i] = '!';
				Guess.set(i, null);
				Solution.remove(i-nR);
				nR++;
			}
		}
		
		for(int i = 0; i < Guess.size(); i++)
		{
			if(Guess.get(i) != null && Solution.contains(Guess.get(i)))
			{
				score[i] = '?';
				Solution.remove(Guess.get(i));
			}
		}
		
		for(int i = 0; i < score.length; i++)
		{
			feedback += "" + score[i];
		}
		return feedback;
	}


	//Returns the length of the words in the list of words
	public int getWordLength() {
		return wordLength;
	}

	//Returns the complete alphabet of chars that are used in any word in the solution list,
	//in order as a String
	public String getAlphabet() {
		return alphabet;
	}


	//Begins a game of Cheatle, initializing any private instance variables necessary for
	// a single game.
	public void beginGame() {
		numGuesses = 0;
		valid.clear();
		eliminated.clear();
		goodPos.clear();
		badPos.clear();
		for(int i = 0; i < alphabet.length(); i++)
		{
			valid.add(""+alphabet.charAt(i));
		}
		solutions = (TreeSet<String>) resetSols.clone();
	}

	//Checks to see if the guess is in the dictionary of words.
	//Does NOT check to see whether the guess is one of the REMAINING words.
	public boolean isAllowable(String guess) {
		return (words.contains(guess));
	}

	//Given a guess, returns a String of '*', '?', and '!'
	//that gives feedback about the corresponding letters in that guess:
	// * means that letter is not in the word
	// ? means that letter is in the word, but not in that place
	// ! means that letter is in that exact place in the word
	// Because this is CHEATLE, not Wordle, you are to return the feedback
	// that leaves the LARGEST possible number of words remaining!!!
	//makeGuess should also UPDATE the list of remaining words
	// and update the list of letters where we KNOW where they are,
	// the list of letters that are definitely in the word but we don't know where they are,
	// the list of letters that are not in the word,
	// and the list of letters that are still possibilities
	public String makeGuess(String guess) {
		String key;
		int val;
		String maxKey = "";
		int maxVal = 0;
		ArrayList<String> toRem = new ArrayList<String>();
		
		HashMap<String, Integer> map = new HashMap<String, Integer> ();
		for(String sol : solutions)
		{
			if(map.containsKey(scoreGuess(guess, sol)))
			{
				map.put(scoreGuess(guess, sol), map.get(scoreGuess(guess, sol)) + 1);
			}
			else
			{
				map.put(scoreGuess(guess, sol), 1);
			}
		}
		
		for(String str : map.keySet())
		{
			if(maxVal < map.get(str) || maxKey.equals(scoreGuess(guess, guess)))
			{
				maxVal = map.get(str);
				maxKey = str;
			}
		}
		numGuesses++;
		
		for(int i = 0; i < guess.length(); i++)
		{
			if(maxKey.charAt(i) == '?')
			{
				if(!badPos.contains(""+guess.charAt(i)))
					badPos.add(""+guess.charAt(i));
			}
			else if(maxKey.charAt(i) == '!')
			{
				if(!goodPos.contains(""+guess.charAt(i)))
					goodPos.add(""+guess.charAt(i));
			}
			else
			{
				if(!eliminated.contains(""+guess.charAt(i)))
					eliminated.add(""+guess.charAt(i));
			}
		}
		
		for(String sol : solutions)
		{
			if(!scoreGuess(guess, sol).equals(maxKey))
			{
				toRem.add(sol);
			}
		}
		for(int i = 0; i < toRem.size(); i++)
		{
			solutions.remove(toRem.get(i));
		}
		
		for(int i = 0; i < guess.length(); i++)
		{
			valid.remove(""+guess.charAt(i));
		}
		
		return maxKey;

	}

	//Returns a String of all letters that have received a ! feedback
	// IN ORDER
	public String correctPlaceLetters() {
		String retVal = "";
		for(String str : goodPos)
		{
			retVal += str;
		}
		return retVal;
	}

	//Returns a String of all letters that have received a ? feedback
	// IN ORDER
	public String wrongPlaceLetters() {
		String retVal = "";
		for(String str : badPos)
		{
			if(!goodPos.contains(str))
				retVal += str;
		}
		return retVal;
	}

	//Returns a String of all letters that have received a * feedback
	// IN ORDER
	public String eliminatedLetters() {
		String retVal = "";
		for(String str : eliminated)
		{
			retVal += str;
		}
		return retVal;
	}

	//Returns a String of all unguessed letters
	public String unguessedLetters() {
		String retVal = "";
		for(String str : valid)
		{
			retVal += str;
		}
		return retVal;
	}


	//Returns true if the feedback string is the winning one,
	//i.e. if it is all !s
	public boolean isWinningFeedback(String feedback) {
		if(feedback.equals(scoreGuess(feedback, feedback)))
			return true;
		return false;
	}

	//Returns a String of all the remaining possible words, with one word per line,
	// IN ORDER
	public String getWordsRemaining() {
		String retVal = "";
		for(String sol : solutions)
		{
			retVal += sol;
			retVal += "\n";
		}
		return retVal;
	}
	
	//Returns the number of possible words remaining
	public int getNumRemaining() {
		return solutions.size();
	}

	//Returns the number of guesses made in this game
	public int numberOfGuessesMade() {
		return numGuesses;
	}

	//Ends the current game and starts a new game.
	public void restart() {
		System.out.println("Welcome to CHEATLE!");
		System.out.println("Type: /restart to restart the game");
		System.out.println("      /quit to quit the game");
		System.out.println("      /remaining to print a complete list of words remaining");
		System.out.println("      /numremaining to print the number of words remaining");
		System.out.println("      /eliminated to see which letters have been eliminated");
		System.out.println("Cheatle will respond to your guess with a sequence of !, ?, and *");
		System.out.println("* means the corresponding letter is not in the word");
		System.out.println("? means the corresponding letter is in the word, but not in the right place");
		System.out.println("! means the corresponding letter is in the correct place in the word");
		System.out.println("You are trying to guess a " + getWordLength() + "-letter word.");
		System.out.println("Good luck!");
		beginGame();
	}

}
