package com.bluntsoftware.ludwig.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inflector {

    protected static final Inflector INSTANCE = new Inflector();

    public static Inflector getInstance() {
        return INSTANCE;
    }

    protected static class Rule {

        protected final String expression;
        protected final Pattern expressionPattern;
        protected final String replacement;

        protected Rule( String expression,
                        String replacement ) {
            this.expression = expression;
            this.replacement = replacement != null ? replacement : "";
            this.expressionPattern = Pattern.compile(this.expression, Pattern.CASE_INSENSITIVE);
        }

        protected String apply( String input ) {
            Matcher matcher = this.expressionPattern.matcher(input);
            if (!matcher.find()) return null;
            return matcher.replaceAll(this.replacement);
        }

        @Override
        public int hashCode() {
            return expression.hashCode();
        }

        @Override
        public boolean equals( Object obj ) {
            if (obj == this) return true;
            if (obj != null && obj.getClass() == this.getClass()) {
                final Rule that = (Rule)obj;
                return this.expression.equalsIgnoreCase(that.expression);
            }
            return false;
        }

        @Override
        public String toString() {
            return expression + ", " + replacement;
        }
    }

    private final LinkedList<Rule> plurals = new LinkedList<>();
    private final LinkedList<Rule> singulars = new LinkedList<>();
    private final Set<String> uncountables = new HashSet<>();

    public Inflector() {
        initialize();
    }

    protected Inflector(Inflector original ) {
        this.plurals.addAll(original.plurals);
        this.singulars.addAll(original.singulars);
        this.uncountables.addAll(original.uncountables);
    }

    @Override
    public Inflector clone() {
      return new Inflector(this);
    }

    public String pluralize( Object word ) {
        if (word == null) return null;
        String wordStr = word.toString().trim();
        if (wordStr.length() == 0) return wordStr;
        if (isUncountable(wordStr)) return wordStr;
        for (Rule rule : this.plurals) {
            String result = rule.apply(wordStr);
            if (result != null) return result;
        }
        return wordStr;
    }

    public String pluralize( Object word,
                             int count ) {
        if (word == null) return null;
        if (count == 1 || count == -1) {
            return word.toString();
        }
        return pluralize(word);
    }

    public String singularize( Object word ) {
        if (word == null) return null;
        String wordStr = word.toString().trim();
        if (wordStr.length() == 0) return wordStr;
        if (isUncountable(wordStr)) return wordStr;
        for (Rule rule : this.singulars) {
            String result = rule.apply(wordStr);
            if (result != null) return result;
        }
        return wordStr;
    }

    public String lowerCamelCase( String lowerCaseAndUnderscoredWord,
                                  char... delimiterChars ) {
        return camelCase(lowerCaseAndUnderscoredWord, false, delimiterChars);
    }

    public String upperCamelCase( String lowerCaseAndUnderscoredWord,
                                  char... delimiterChars ) {
        return camelCase(lowerCaseAndUnderscoredWord, true, delimiterChars);
    }

    public String camelCase( String lowerCaseAndUnderscoredWord,
                             boolean uppercaseFirstLetter,
                             char... delimiterChars ) {
        if (lowerCaseAndUnderscoredWord == null) return null;
        lowerCaseAndUnderscoredWord = lowerCaseAndUnderscoredWord.trim();
        if (lowerCaseAndUnderscoredWord.length() == 0) return "";
        if (uppercaseFirstLetter) {
            String result = lowerCaseAndUnderscoredWord;
            // Replace any extra delimiters with underscores (before the underscores are converted in the next step)...
            if (delimiterChars != null) {
                for (char delimiterChar : delimiterChars) {
                    result = result.replace(delimiterChar, '_');
                }
            }

            // Change the case at the beginning at after each underscore ...
            return replaceAllWithUppercase(result, "(^|_)(.)", 2);
        }
        if (lowerCaseAndUnderscoredWord.length() < 2) return lowerCaseAndUnderscoredWord;
        return "" + Character.toLowerCase(lowerCaseAndUnderscoredWord.charAt(0))
                + camelCase(lowerCaseAndUnderscoredWord, true, delimiterChars).substring(1);
    }


    public String underscore( String camelCaseWord,
                              char... delimiterChars ) {
        if (camelCaseWord == null) return null;
        String result = camelCaseWord.trim();
        if (result.length() == 0) return "";
        result = result.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2");
        result = result.replaceAll("([a-z\\d])([A-Z])", "$1_$2");
        result = result.replace('-', '_');
        if (delimiterChars != null) {
            for (char delimiterChar : delimiterChars) {
                result = result.replace(delimiterChar, '_');
            }
        }
        return result.toLowerCase();
    }

    public String capitalize( String words ) {
        if (words == null) return null;
        String result = words.trim();
        if (result.length() == 0) return "";
        if (result.length() == 1) return result.toUpperCase();
        return "" + Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }

    public String humanize( String lowerCaseAndUnderscoredWords,
                            String... removableTokens ) {
        if (lowerCaseAndUnderscoredWords == null) return null;
        String result = lowerCaseAndUnderscoredWords.trim();
        if (result.length() == 0) return "";
        // Remove a trailing "_id" token
        result = result.replaceAll("_id$", "");
        // Remove all of the tokens that should be removed
        if (removableTokens != null) {
            for (String removableToken : removableTokens) {
                result = result.replaceAll(removableToken, "");
            }
        }
        result = result.replaceAll("_+", " "); // replace all adjacent underscores with a single space
        return capitalize(result);
    }

    public String titleCase( String words,
                             String... removableTokens ) {
        String result = humanize(words, removableTokens);
        result = replaceAllWithUppercase(result, "\\b([a-z])", 1); // change first char of each word to uppercase
        return result;
    }

    public String ordinalize( int number ) {
        int remainder;
        String numberStr = Integer.toString(number);
        if (11 <= number && number <= 13) return numberStr + "th";
        remainder = number % 10;
        if (remainder == 1) return numberStr + "st";
        if (remainder == 2) return numberStr + "nd";
        if (remainder == 3) return numberStr + "rd";
        return numberStr + "th";
    }

    public boolean isUncountable( String word ) {
        if (word == null) return false;
        String trimmedLower = word.trim().toLowerCase();
        return this.uncountables.contains(trimmedLower);
    }

    public Set<String> getUncountables() {
        return uncountables;
    }

    public void addPluralize( String rule,
                              String replacement ) {
        final Rule pluralizeRule = new Rule(rule, replacement);
        this.plurals.addFirst(pluralizeRule);
    }

    public void addSingularize( String rule,
                                String replacement ) {
        final Rule singularizeRule = new Rule(rule, replacement);
        this.singulars.addFirst(singularizeRule);
    }

    public void addIrregular( String singular, String plural ) {
        String singularRemainder = singular.length() > 1 ? singular.substring(1) : "";
        String pluralRemainder = plural.length() > 1 ? plural.substring(1) : "";
        addPluralize("(" + singular.charAt(0) + ")" + singularRemainder + "$", "$1" + pluralRemainder);
        addSingularize("(" + plural.charAt(0) + ")" + pluralRemainder + "$", "$1" + singularRemainder);
    }

    public void addUncountable( String... words ) {
        if (words == null || words.length == 0) return;
        for (String word : words) {
            if (word != null) uncountables.add(word.trim().toLowerCase());
        }
    }

    protected static String replaceAllWithUppercase( String input,
                                                     String regex,
                                                     int groupNumberToUppercase ) {
        Pattern underscoreAndDotPattern = Pattern.compile(regex);
        Matcher matcher = underscoreAndDotPattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(groupNumberToUppercase).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public void clear() {
        this.uncountables.clear();
        this.plurals.clear();
        this.singulars.clear();
    }

    protected void initialize() {
        Inflector inflect = this;
        inflect.addPluralize("$", "s");
        inflect.addPluralize("s$", "s");
        inflect.addPluralize("(ax|test)is$", "$1es");
        inflect.addPluralize("(octop|vir)us$", "$1i");
        inflect.addPluralize("(octop|vir)i$", "$1i"); // already plural
        inflect.addPluralize("(alias|status)$", "$1es");
        inflect.addPluralize("(bu)s$", "$1ses");
        inflect.addPluralize("(buffal|tomat)o$", "$1oes");
        inflect.addPluralize("([ti])um$", "$1a");
        inflect.addPluralize("([ti])a$", "$1a"); // already plural
        inflect.addPluralize("sis$", "ses");
        inflect.addPluralize("(?:([^f])fe|([lr])f)$", "$1$2ves");
        inflect.addPluralize("(hive)$", "$1s");
        inflect.addPluralize("([^aeiouy]|qu)y$", "$1ies");
        inflect.addPluralize("(x|ch|ss|sh)$", "$1es");
        inflect.addPluralize("(matr|vert|ind)ix|ex$", "$1ices");
        inflect.addPluralize("([m|l])ouse$", "$1ice");
        inflect.addPluralize("([m|l])ice$", "$1ice");
        inflect.addPluralize("^(ox)$", "$1en");
        inflect.addPluralize("(quiz)$", "$1zes");
        // Need to check for the following words that are already pluralized:
        inflect.addPluralize("(people|men|children|sexes|moves|stadiums)$", "$1"); // irregulars
        inflect.addPluralize("(oxen|octopi|viri|aliases|quizzes)$", "$1"); // special rules

        inflect.addSingularize("s$", "");
        inflect.addSingularize("(s|si|u)s$", "$1s"); // '-us' and '-ss' are already singular
        inflect.addSingularize("(n)ews$", "$1ews");
        inflect.addSingularize("([ti])a$", "$1um");
        inflect.addSingularize("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis");
        inflect.addSingularize("(^analy)ses$", "$1sis");
        inflect.addSingularize("(^analy)sis$", "$1sis"); // already singular, but ends in 's'
        inflect.addSingularize("([^f])ves$", "$1fe");
        inflect.addSingularize("(hive)s$", "$1");
        inflect.addSingularize("(tive)s$", "$1");
        inflect.addSingularize("([lr])ves$", "$1f");
        inflect.addSingularize("([^aeiouy]|qu)ies$", "$1y");
        inflect.addSingularize("(s)eries$", "$1eries");
        inflect.addSingularize("(m)ovies$", "$1ovie");
        inflect.addSingularize("(x|ch|ss|sh)es$", "$1");
        inflect.addSingularize("([m|l])ice$", "$1ouse");
        inflect.addSingularize("(bus)es$", "$1");
        inflect.addSingularize("(o)es$", "$1");
        inflect.addSingularize("(shoe)s$", "$1");
        inflect.addSingularize("(cris|ax|test)is$", "$1is"); // already singular, but ends in 's'
        inflect.addSingularize("(cris|ax|test)es$", "$1is");
        inflect.addSingularize("(octop|vir)i$", "$1us");
        inflect.addSingularize("(octop|vir)us$", "$1us"); // already singular, but ends in 's'
        inflect.addSingularize("(alias|status)es$", "$1");
        inflect.addSingularize("(alias|status)$", "$1"); // already singular, but ends in 's'
        inflect.addSingularize("^(ox)en", "$1");
        inflect.addSingularize("(vert|ind)ices$", "$1ex");
        inflect.addSingularize("(matr)ices$", "$1ix");
        inflect.addSingularize("(quiz)zes$", "$1");

        inflect.addIrregular("person", "people");
        inflect.addIrregular("man", "men");
        inflect.addIrregular("child", "children");
        inflect.addIrregular("sex", "sexes");
        inflect.addIrregular("move", "moves");
        inflect.addIrregular("stadium", "stadiums");

        inflect.addUncountable("equipment", "information", "rice", "money", "species", "series", "fish", "sheep");
    }

}
