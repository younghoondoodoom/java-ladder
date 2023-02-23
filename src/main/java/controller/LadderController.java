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

    private static final int NAMES_WIDTH_DIFFERENCE = 1;
    private static final String ALL = "all";
    public static final String NAME_NOT_FOUND_ERROR_MESSAGE = "해당 이름이 존재하지 않습니다.";
    public static final String NAME_RESULT_COUNT_NOT_MATCH_MESSAGE = "결과 개수와 이름의 개수가 일치하지 않습니다.";
    public static final String DUPLICATE_NAME_ERROR_MESSAGE = "중복된 이름은 허용되지 않습니다.";
    private final InputView inputView;
    private final OutputView outputView;
    private final LadderGameSupport ladderGameSupport;

    public LadderController(final InputView inputView, final OutputView outputView,
        final LadderGameSupport ladderGameSupport) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.ladderGameSupport = ladderGameSupport;
    }

    public void play() {
        final List<Name> names = getNames();
        final List<Result> results = getResults(names.size());
        final Height height = getHeight();
        final Width width = new Width(names.size() - NAMES_WIDTH_DIFFERENCE);
        final Ladder ladder = ladderGameSupport.makeLadder(height, width);
        outputView.printLadder(names, ladder, results);
        final Map<Name, Result> resultBoard = ladderGameSupport.makeResultBoard(ladder, names, results);
        ExecuteContext.workWithExecuteStrategy(() -> printResult(resultBoard));
    }

    private Boolean printResult(final Map<Name, Result> resultBoard) {
        final String input = inputView.inputResultTarget();
        final Name name = new Name(input);
        if (name.getValue().equals(ALL)) {
            outputView.printAllResult(resultBoard);
            return true;
        }
        printOneResult(resultBoard, name);
        printResult(resultBoard);
        return null;
    }

    private void printOneResult(final Map<Name, Result> resultBoard, final Name name) {
        if (resultBoard.containsKey(name)) {
            outputView.printResult(resultBoard.get(name));
            return;
        }
        throw new IllegalArgumentException(NAME_NOT_FOUND_ERROR_MESSAGE);
    }

    private List<Name> getNames() {
        return ExecuteContext.workWithExecuteStrategy(() -> {
            final List<Name> names = inputView.inputNames()
                .stream()
                .map(Name::new)
                .collect(Collectors.toList());
            checkNameDuplicate(names);
            return names;
        });
    }

    private Height getHeight() {
        return ExecuteContext.workWithExecuteStrategy(
            () -> new Height(inputView.inputLadderHeight()));
    }

    private List<Result> getResults(final int namesSize) {
        return ExecuteContext.workWithExecuteStrategy(() -> {
                final List<Result> results = inputView.inputResults()
                    .stream()
                    .map(Result::new)
                    .collect(Collectors.toList());
                checkNamesAndResultsSize(namesSize, results);
                return results;
            }
        );
    }

    private void checkNamesAndResultsSize(final int namesSize, final List<Result> results) {
        if (results.size() != namesSize) {
            throw new IllegalArgumentException(NAME_RESULT_COUNT_NOT_MATCH_MESSAGE);
        }
    }

    private void checkNameDuplicate(final List<Name> names) {
        if (names.stream().distinct().count() != names.size()) {
            throw new IllegalArgumentException(DUPLICATE_NAME_ERROR_MESSAGE);
        }
    }
}
