package domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class WidthTest {

    @ParameterizedTest(name = "{0} 너비는 허용된다.")
    @ValueSource(strings = {"1", "99"})
    void makeWidthSuccess(int provided) {
        assertThatNoException().isThrownBy(() -> new Width(provided));
    }

    @ParameterizedTest(name = "{0} 너비는 허용되지 않는다.")
    @ValueSource(strings = {"0", "100"})
    void makeWidthFailure(int provided) {
        assertThatThrownBy(() -> new Width(provided)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("동등성 비교 테스트")
    public void equalsTest() {
        //given
        int value = 10;
        Width target = new Width(value);

        //when
        boolean result = target.equals(new Width(10));

        //then
        assertThat(result).isTrue();
    }
}
