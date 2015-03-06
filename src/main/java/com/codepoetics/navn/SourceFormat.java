package com.codepoetics.navn;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum SourceFormat implements Predicate<String>, Function<String, String[]> {
    WHITESPACE_SEPARATED() {
        @Override
        public boolean test(String s) {
            return s.chars().anyMatch(Character::isWhitespace);
        }

        @Override
        public String[] apply(String s) {
            return s.split("\\s+");
        }
    },
    UNDERSCORE_SEPARATED() {
        @Override
        public boolean test(String s) {
            return s.contains("_");
        }

        @Override
        public String[] apply(String input) {
            return Stream.of(input.split("_+")).filter(s -> s.length() > 0).toArray(String[]::new);
        }
    },
    CASE_SEPARATED() {
        private final ThreadLocal<CaseSeparatedReader> readerThreadLocal = ThreadLocal.withInitial(CaseSeparatedReader::new);

        @Override
        public boolean test(String s) {
            return s.chars().anyMatch(Character::isLowerCase) &&
                    s.chars().anyMatch(Character::isUpperCase);
        }

        @Override
        public String[] apply(String s) {
            return readerThreadLocal.get().read(s);
        }
    }
}
