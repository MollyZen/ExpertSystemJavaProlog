package prolog;

import org.jpl7.Term;
import org.jpl7.Util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Question {
    public Integer id = -1;
    public String value = "";

    public Question() {
    }

    public Question(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Term toTerm(){
        return Term.textToTerm(
                "question(" +
                        Arrays.stream(new String[]{id.toString(), "\"" + value + "\""}).collect(Collectors.joining(",")) +
                ")");
    }
}
