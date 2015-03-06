package com.codepoetics.navn;

public enum FormattingOptions implements FormattingOption {
    LOWERCASE() {
        @Override
        public String apply(String s, Long position) {
            return s.toLowerCase();
        }
    },
    UPPERCASE() {
        @Override
        public String apply(String s, Long position) {
            return s.toUpperCase();
        }
    },
    CAPITALISE_ALL() {
        @Override
        public String apply(String s, Long position) {
            if (s.isEmpty()) {
                return s;
            }
            if (s.chars().allMatch(Character::isUpperCase)) {
                return s;
            }
            return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
        }
    },
    CAPITALISE_ALL_BUT_FIRST() {
        @Override
        public String apply(String s, Long position) {
            if (s.chars().allMatch(Character::isUpperCase)) {
                return s;
            }
            if (position == 0) {
                return s.toLowerCase();
            }
            return CAPITALISE_ALL.apply(s, position);
        }
    }
}
