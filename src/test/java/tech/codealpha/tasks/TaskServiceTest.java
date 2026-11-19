package tech.codealpha.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskServiceTest {
    private final TaskService service = new TaskService();

    @Test
    void createTrimsTitleAndAssignsIncreasingIds() {
        Task a = service.create("  write docs ");
        Task b = service.create("ship it");
        assertEquals("write docs", a.title());
        assertEquals(1, a.id());
        assertEquals(2, b.id());
        assertFalse(a.done());
    }

    @Test
    void createRejectsBlankAndTooLongTitles() {
        assertThrows(IllegalArgumentException.class, () -> service.create("   "));
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
        assertThrows(IllegalArgumentException.class, () -> service.create("x".repeat(201)));
    }

    @Test
    void updateChangesOnlyProvidedFields() {
        Task t = service.create("old");
        Task done = service.update(t.id(), null, true).orElseThrow();
        assertEquals("old", done.title());
        assertTrue(done.done());
        Task renamed = service.update(t.id(), "new", null).orElseThrow();
        assertEquals("new", renamed.title());
        assertTrue(renamed.done());
        assertTrue(service.update(999, "x", null).isEmpty());
    }

    @Test
    void deleteRemovesTask() {
        Task t = service.create("temp");
        assertTrue(service.delete(t.id()));
        assertFalse(service.delete(t.id()));
        assertTrue(service.list().isEmpty());
    }
}
