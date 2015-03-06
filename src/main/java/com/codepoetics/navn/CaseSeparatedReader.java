package com.codepoetics.navn;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

final class CaseSeparatedReader {

    private static interface StateFunction extends BiFunction<Character, CaseSeparatedReader, State> {
    }

    private static enum State implements StateFunction {
        INITIAL() {
            @Override
            public State apply(Character c, CaseSeparatedReader reader) {
                return Character.isUpperCase(c)
                    ? READING_UPPERCASE : READING_LOWERCASE;
            }
        },

        READING_LOWERCASE() {
            @Override
            public State apply(Character c, CaseSeparatedReader reader) {
                return Character.isUpperCase(c)
                    ? reader.pushName(READING_UPPERCASE) : READING_LOWERCASE;
            }
        },

        READING_ACRONYM() {
            @Override
            public State apply(Character c, CaseSeparatedReader reader) {
                return Character.isUpperCase(c)
                    ? State.READING_ACRONYM : reader.pushAcronym(READING_LOWERCASE);
            }
        },

        READING_UPPERCASE() {
            @Override
            public State apply(Character c, CaseSeparatedReader reader) {
                return Character.isUpperCase(c)
                        ? READING_ACRONYM : READING_LOWERCASE;
            }
        }
    }

    private State state;
    private List<String> parts;
    private StringBuilder currentName;

    private void clearCurrentName() {
        currentName.setLength(0);
    }

    private void addPart(String part) {
        parts.add(part);
    }

    private void addChar(char c) {
        currentName.append(c);
    }

    private State pushName(State newState) {
        addPart(currentName.toString());
        clearCurrentName();
        return newState;
    }

    private State pushAcronym(State newState) {
        addPart(currentName.substring(0, currentName.length() - 1));
        char lastChar = currentName.charAt(currentName.length() - 1);
        clearCurrentName();
        addChar(lastChar);
        return newState;
    }

    private void initialise() {
        state = State.INITIAL;
        parts = new LinkedList<>();
        currentName = new StringBuilder();
    }

    public String[] read(String input) {
        initialise();
        input.chars().forEach(this::readCharacter);
        if (currentName.length() > 0) {
            pushName(null);
        }
        return parts.stream().toArray(String[]::new);
    }

    private void readCharacter(int c) {
        state = state.apply((char) c, this);
        addChar((char) c);
    }
}
