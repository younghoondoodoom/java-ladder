package controller;

import common.ExecuteContext;
import domain.model.Ladder;
import domain.service.LadderGameSupport;
import domain.vo.Height;
import domain.vo.Name;
import domain.vo.Result;
import domain.vo.Width;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import view.InputView;
import view.OutputView;

public class LadderController {

    public static final String NAME_RESULT_COUNT_NOT_MATCH_MESSAGE = "결과 개수와 이름의 개수가 일치하지 않습니다.";
    private static final int NAMES_WIDTH_DIFFERENCE = 1;
    private static final String ALL = "all";
    public static final String NAME_NOT_FOUND = "해당 이름이 존재하지 않습니다.";
    private final InputView inputView;
    private final OutputView outputView;
    private final LadderGameSupport ladderGameSupport;

    public LadderController(InputView inputView, OutputView outputView,
        LadderGameSupport ladderGameSupport) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.ladderGameSupport = ladderGameSupport;
    }

    public void play() {
        List<Name> names = getNames();
        List<Result> results = getResults(names.size());
        Height height = getHeight();
        Ladder ladder = ladderGameSupport.makeLadder(height,
            new Width(names.size() - NAMES_WIDTH_DIFFERENCE));
        outputView.printLadder(names, ladder, results);
        Map<Name, Result> resultBoard = ladderGameSupport.makeResultBoard(ladder, names, results);
        printResult(resultBoard);
    }

    private void printResult(Map<Name, Result> resultBoard) {
        Name name = ExecuteContext.workWithExecuteStrategy(
            () -> new Name(inputView.inputResultTarget()));
        if (name.getValue().equals(ALL)) {
            outputView.printAllResult(resultBoard);
            return;
        }
        printOneResult(resultBoard, name);
        printResult(resultBoard);
    }

    private void printOneResult(Map<Name, Result> resultBoard, Name name) {
        if (resultBoard.containsKey(name)) {
            outputView.printResult(resultBoard.get(name));
            return;
        }
        throw new IllegalArgumentException(NAME_NOT_FOUND);
    }

    private List<Name> getNames() {
        return ExecuteContext.workWithExecuteStrategy(() -> inputView.inputNames()
            .stream()
            .map(Name::new)
            .collect(Collectors.toList()));
    }

    private Height getHeight() {
        return ExecuteContext.workWithExecuteStrategy(
            () -> new Height(inputView.inputLadderHeight()));
    }

    private List<Result> getResults(final int namesSize) {
        return ExecuteContext.workWithExecuteStrategy(() -> {
                List<Result> results = inputView.inputResults()
                    .stream()
                    .map(Result::new)
                    .collect(Collectors.toList());
                checkNamesAndResultsSize(namesSize, results);
                return results;
            }
        );
    }

    private static void checkNamesAndResultsSize(int namesSize, List<Result> results) {
        if (results.size() != namesSize) {
            throw new IllegalArgumentException(NAME_RESULT_COUNT_NOT_MATCH_MESSAGE);
        }
    }
}
