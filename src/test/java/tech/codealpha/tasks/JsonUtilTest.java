package tech.codealpha.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class JsonUtilTest {
    @Test
    void serialisesTaskAndEscapesSpecialCharacters() {
        String json = JsonUtil.toJson(new Task(7, "say \"hi\"\nnow", true));
        assertEquals("{\"id\":7,\"title\":\"say \\\"hi\\\"\\nnow\",\"done\":true}", json);
    }

    @Test
    void serialisesList() {
        assertEquals("[]", JsonUtil.toJson(List.of()));
        assertEquals("[{\"id\":1,\"title\":\"a\",\"done\":false}]", JsonUtil.toJson(List.of(new Task(1, "a", false))));
    }

    @Test
    void extractsValuesAndRoundTripsEscapes() {
        String body = "{ \"title\" : \"a \\\"quoted\\\" word\", \"done\": true }";
        assertEquals("a \"quoted\" word", JsonUtil.extractString(body, "title"));
        assertEquals(Boolean.TRUE, JsonUtil.extractBoolean(body, "done"));
        assertNull(JsonUtil.extractString(body, "missing"));
        assertNull(JsonUtil.extractBoolean(body, "missing"));
    }
}
