import prolog.*;

import java.util.HashMap;
import java.util.List;

public class SystemCreationChecker{

    public Conditions conds;
    public Questions questions;
    public Rules rules;

    public HashMap<Integer, Condition> condIdMap = new HashMap<>();
    public HashMap<Integer, Question> queIdMap = new HashMap<>();

    public SystemCreationChecker(Conditions conds, Questions questions, Rules rules) {
        this.conds = conds;
        this.questions = questions;
        this.rules = rules;
    }

    public boolean isThisDuplicateCondId(Condition cond){
        Condition tmpCond = condIdMap.putIfAbsent(cond.id, cond);
        if (tmpCond == null){
            return false;
        }
        else {
            return !tmpCond.equals(cond);
        }
    }

    public boolean isThisDuplicateQuestionId(Question que){
        Question tmpCond = queIdMap.putIfAbsent(que.id, que);
        if (tmpCond == null){
            return false;
        }
        else {
            return !tmpCond.equals(que);
        }
    }

    public boolean doAllCondsExists(List<Integer> conds){
        return conds.stream().allMatch(val -> this.conds.list.stream().anyMatch(cond -> cond.id.equals(val)));
    }

    public boolean questionExists(Integer question){
        if (question.equals(-1)){
            return true;
        }
        for (Question que: questions.list){
            if (que.id.equals(question)){
                return true;
            }
        }
        return false;
    }
}
