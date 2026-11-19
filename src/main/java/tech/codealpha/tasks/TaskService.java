package tech.codealpha.tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Thread-safe in-memory task store with validation. */
public class TaskService {
    public static final int MAX_TITLE_LENGTH = 200;

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Task create(String title) {
        String clean = validateTitle(title);
        long id = sequence.incrementAndGet();
        Task task = new Task(id, clean, false);
        tasks.put(id, task);
        return task;
    }

    public List<Task> list() {
        List<Task> all = new ArrayList<>(tasks.values());
        all.sort(Comparator.comparingLong(Task::id));
        return all;
    }

    public Optional<Task> get(long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    /** Updates title and/or done flag; null arguments leave the field unchanged. */
    public Optional<Task> update(long id, String title, Boolean done) {
        String clean = title == null ? null : validateTitle(title);
        return Optional.ofNullable(tasks.computeIfPresent(id, (key, old) ->
                new Task(old.id(), clean != null ? clean : old.title(), done != null ? done : old.done())));
    }

    public boolean delete(long id) {
        return tasks.remove(id) != null;
    }

    private static String validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        String trimmed = title.trim();
        if (trimmed.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("title must be at most " + MAX_TITLE_LENGTH + " characters");
        }
        return trimmed;
    }
}
