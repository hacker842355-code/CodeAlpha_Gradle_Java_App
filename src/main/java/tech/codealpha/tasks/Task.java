package tech.codealpha.tasks;

/** Immutable task value object. */
public record Task(long id, String title, boolean done) {
}
