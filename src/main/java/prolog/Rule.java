package prolog;

import org.jpl7.Term;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    public Boolean result = true;
    public List<Integer> conditions = new ArrayList<>();

    public Rule() {
    }

    public Rule(Boolean result, List<Integer> conditions) {
        this.result = result;
        this.conditions = conditions;
    }

    public Term toTerm(){
        return Term.textToTerm(
                "rule(" +
                        result.toString() + "," +
                        conditions.toString().replaceAll(" ","") +
                        ")");
    }
}
