package edu.uci.ics.textdb.dataflow.regexmatch;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.re2j.PublicParser;
import com.google.re2j.PublicRE2;
import com.google.re2j.PublicRegexp;
import com.google.re2j.PublicSimplify;

/**
 * This class translates a regex to a boolean query of n-grams,
 * according to the <a href='https://swtch.com/~rsc/regexp/regexp4.html'>algorithm</a> 
 * described in Russ Cox's article. <br>
 * 
 * @Author Zuozhi Wang
 * @Author Shuying Lai
 * 
 */
public class RegexToGramQueryTranslator {	

	/**
	 * This method translates a regular expression to 
	 * a boolean expression of n-grams. <br>
	 * Then the boolean expression can be queried using 
	 * an n-gram inverted index to speed up regex matching. <br>
	 * 
	 * @param regex, the regex string to be translated.
	 * @return GamBooleanQeruy, a boolean query of n-grams.
	 */
	public static GramBooleanQuery translate(String regex) {
		// try to parse using RE2J
		try {
		    PublicRegexp re = PublicParser.parse(regex, PublicRE2.PERL);
		    re = PublicSimplify.simplify(re);
		    RegexInfo regexInfo = analyze(re);
		    return regexInfo.match;
		    // if RE2J parsing fails
		} catch (com.google.re2j.PatternSyntaxException re2j_e) {
			// try to parse using Java Regex
			// if succeeds, return matchAll (scan based)
			try {
				java.util.regex.Pattern.compile(regex);
				return RegexInfo.matchAny().match;
			// if Java Regex fails too, return matchNone (not a regex)
			} catch (java.util.regex.PatternSyntaxException java_e) {
				return RegexInfo.matchNone().match;
			}
		}
	}
	
	
	/**
	 * This is the main function of analyzing a regular expression. <br>
	 * This methods walks through the regex abstract syntax tree generated by RE2J, 
	 * and 
	 * 
	 * @param PublicRegexp
	 * @return RegexInfo
	 */
	private static RegexInfo analyze(PublicRegexp re) {
		RegexInfo info = new RegexInfo();
		switch (re.getOp()) {
		// NO_MATCH is a regex that doesn't match anything.
		// It's used to handle error cases, which shouldn't 
		// happen unless something goes wrong.
		case NO_MATCH: 
		case VERTICAL_BAR:
		case LEFT_PAREN: {
			return RegexInfo.matchNone();
		}
		// The following cases are treated as 
		// a regex that matches an empty string.
		case EMPTY_MATCH:
		case WORD_BOUNDARY:	case NO_WORD_BOUNDARY:
		case BEGIN_LINE: 	case END_LINE:
		case BEGIN_TEXT: 	case END_TEXT: {
			return RegexInfo.emptyString();
		}
		// A regex that matches any character
		case ANY_CHAR: case ANY_CHAR_NOT_NL: {
			return RegexInfo.anyChar();
		}
		// TODO finish for every case
		case ALTERNATE:
			//TODO
			return fold((x, y) -> alternate(x, y), re.getSubs(), RegexInfo.matchNone());
		case CAPTURE:
			//TODO
			return RegexInfo.matchAny();
		// For example, [a-z]
		case CHAR_CLASS:
			boolean isCaseSensitive = (re.getFlags() & PublicRE2.FOLD_CASE) > 0;
			
			if (re.getRunes().length == 0) {
				return RegexInfo.matchNone();
			} else if (re.getRunes().length == 1) {
				String exactStr;
				if (isCaseSensitive) {
					exactStr = Character.toString((char) re.getRunes()[0]);
				} else {
					exactStr = Character.toString((char) re.getRunes()[0]).toLowerCase();
				}
				info.exact.add(exactStr);
				return info;
			}
			
			// convert all runes to lower case if not case sensitive
			if (!isCaseSensitive) {
				for (int i = 0; i < re.getRunes().length; i++) {
					re.getRunes()[i] = Character.toLowerCase(re.getRunes()[i]);
				}
			}
			
			int count = 0;
			for (int i = 0; i < re.getRunes().length; i += 2) {
				count += re.getRunes()[i+1] - re.getRunes()[i];
				// If the class is too large, it's okay to overestimate.
				if (count > 100) { 
					return RegexInfo.matchAny();
				}
				
				for (int codePoint = re.getRunes()[i]; codePoint <= re.getRunes()[i+1]; codePoint ++) {
					info.exact.add(Character.toString((char) codePoint));
				}
			}
			return info;
		case CONCAT:
			return fold((x, y) -> concat(x, y), re.getSubs(), RegexInfo.matchAny());
		case LITERAL:
			if (re.getRunes().length == 0) {
				return RegexInfo.emptyString();
			}
			
			String literal = "";
			if ((re.getFlags() & PublicRE2.FOLD_CASE) != 0) {  // case sensitive
				for (int rune: re.getRunes()) {
					literal += Character.toString((char) rune);
				}
			} else {
				for (int rune: re.getRunes()) {
					literal += Character.toString((char) rune).toLowerCase();
				}
			}
			info = new RegexInfo();
			info.exact.add(literal);
			return info;
		// A regex that indicates an expression is matched 
		// at least min times, at most max times.
		case REPEAT:
			// When min is zero, we treat REPEAT as START
			// When min is greater than zero, we treat REPEAT as PLUS, and let it fall through.
			if (re.getMin() == 0) {
				return RegexInfo.matchAny();
			}
			// !!!!! intentionally FALL THROUGH to PLUS !!!!!
		// A regex that indicates one or more occurrences of an expression.
		case PLUS:
			// The regexInfo of "(expr)+" should be the same as the info of "expr", 
			// except that "exact" is null, because we don't know the number of repetitions.
			info = analyze(re.getSubs()[0]);
			info.exact = null;
			return info;
		case QUEST:
			// The regexInfo of "(expr)?" shoud be either the same as the info of "expr",
			// or the same as the info of an empty string.
			return alternate(analyze(re.getSubs()[0]), RegexInfo.emptyString());
		// A regex that indicates zero or more occurrences of an expression.
		case STAR:
			return RegexInfo.matchAny();
		default:
			return RegexInfo.matchAny();
		}
	}
	
	@FunctionalInterface
	private static interface TranslatorFunc{
		RegexInfo func(RegexInfo x, RegexInfo y);
	}
	
	private static RegexInfo alternate(RegexInfo x, RegexInfo y) {
		RegexInfo alternateInfo = new RegexInfo();
		//TODO
		return x;
	}
	
	private static RegexInfo concat(RegexInfo x, RegexInfo y) {
		// TODO
		return x;
	}
	
	private static RegexInfo fold (TranslatorFunc func, PublicRegexp[] subExpressions, RegexInfo zero) {
		
		return null;
	}
	
}
