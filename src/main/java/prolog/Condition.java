package prolog;

import org.jpl7.Atom;
import org.jpl7.Compound;
import org.jpl7.Term;

public class Condition {
    public Integer id = -1;
    public String desc = "";
    public Integer ass_question = -1;
    public String expl = "";

    public Condition() {
    }

    public Condition(Integer id, String desc, Integer ass_question, String expl) {
        this.id = id;
        this.desc = desc;
        this.ass_question = ass_question;
        this.expl = expl;
    }

    public Term toTerm(){
        return new Compound("cond", new Term[]{new org.jpl7.Integer(id), new Atom(desc,"string"), new org.jpl7.Integer(ass_question), new Atom(expl, "string")});
    }
}
