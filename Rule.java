/**
 * A single traffic rule.
 *
 * The radar doesn't know anything about speed limits or seat belts - it just
 * asks every rule it was given "does this observation break you?". To add a
 * new rule to the system you write a class that implements this interface
 * and register it, nothing else needs to change.
 */
public interface Rule {

    /**
     * Name used when reporting how many times each rule got broken.
     */
    String getName();

    /**
     * Checks the observation against this rule.
     * Returns the violation if the rule was broken, or null if everything is fine.
     */
    Violation evaluate(Observation observation);
}
