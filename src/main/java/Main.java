import org.jpl7.Compound;
import org.jpl7.JPL;
import org.jpl7.Query;
import org.jpl7.Term;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //инициализация функций, которые будут нужны вдальнейшем
        List<Term> rulesToAdd = new ArrayList<>();
        //вывод вопроса в окно
        rulesToAdd.add(Term.textToTerm("askQuestion(CondNum, -1) :- ref(Ref), cond(CondNum, Question, _, _), jpl_call(Ref, saveCond, [CondNum],_), concat(Question, '?', Dr), string_to_atom(Dr, Drip), jpl_call(Ref, setQuestion, [Drip], _)"));
        rulesToAdd.add(Term.textToTerm("askQuestion(CondNum,QNum) :- ref(Ref), question(QNum, Question), string_to_atom(Question, Drip), jpl_call(Ref, setQuestion, [Drip], _)"));

        //вывод кнопок
        rulesToAdd.add(Term.textToTerm("addAssConds(QNum) :- ref(Ref), jpl_call(Ref, setButtons, [QNum], _)"));

        //проверка на то, есть ли все элементы списка
        rulesToAdd.add(Term.textToTerm("checkList([]) :- not(fail)"));

        //для ожидания
        rulesToAdd.add(Term.textToTerm("updated(0)"));
        rulesToAdd.add(Term.textToTerm("interrupted(0)"));
        rulesToAdd.add(
                Term.textToTerm(
                        "checkList([H|T]) :- not(interrupted(1)), fact(H,_,1), !, checkList(T);" +
                                "not(interrupted(1)), fact(H,_,0),!,fail,!;" +
                                "not(interrupted(1)), cond(H,_,AssQ,_), fact(_,AssQ,_),!, fail,!;" +
                                "not(interrupted(1)), cond(H,_,AssQ,_), askQuestion(H,AssQ), addAssConds(AssQ), thread_wait(updated(1),[]), !, not(interrupted(1)), fact(H,_,1), !, checkList(T);" +
                                "!,fail,!"));

        //ключевая обработка
        rulesToAdd.add(Term.textToTerm("work(Answ, List) :- not(interrupted(1)), !, rule(Res, List), checkList(List), Answ = Res,!"));

        //следующие два нужны чтоб можно было загружать новые файлы и корректно отображать содержание
        rulesToAdd.add(Term.textToTerm("file(drip)"));
        rulesToAdd.add(new Compound("ref", new Term[]{JPL.newJRef(null)}));

        //нужно чтоб искать факты
        rulesToAdd.add(Term.textToTerm("fact(drip,drip,drip)"));

        for (Term term : rulesToAdd) {
            new Query(new Compound("assertz", new Term[]{term})).hasSolution();
        }

        WelcomeWindow welcome = new WelcomeWindow();
        welcome.run();
        welcome.setVisible(true);
    }
}
