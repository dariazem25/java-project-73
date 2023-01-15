package hexlet.code;

import hexlet.code.config.SpringConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SpringConfig.class)
class AppTest {

    @Test
    void testInit() {
        assertThat(true).isTrue();
    }
}
