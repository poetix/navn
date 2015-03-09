package com.codepoetics.navn;

import com.codepoetics.protonpack.StreamUtils;
import com.codepoetics.protonpack.Streamable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Name {

    public static Name empty() {
        return new Name(Streamable.empty(), 0);
    }

    public static Name of(String source) {
        String trimmed = source.trim();
        if (trimmed.isEmpty()) {
            return empty();
        }
        return Stream.of(SourceFormat.values())
                .filter(format -> format.test(trimmed))
                .map(format -> ofTrimmed(trimmed, format))
                .findFirst()
                .orElseGet(() -> new Name(Streamable.of(trimmed), 1));
    }

    public static Name of(String source, char separator) {
        return of(source, "\\" + separator + "+");
    }

    public static Name of(String source, String regex) {
        return of(source, s -> s.split(regex));
    }

    public static Name of(String source, Function<String, String[]> reader) {
        return ofTrimmed(source, reader);
    }

    private static Name ofTrimmed(String trimmed, Function<String, String[]> reader) {
        return of(reader.apply(trimmed));
    }

    public static Name of(String[] parts) {
        return new Name(Streamable.of(parts), parts.length);
    }

    public static Name of(Collection<String> parts) {
        return new Name(Streamable.of(parts), parts.size());
    }

    public static Name of(Iterable<String> parts) {
        return of(Streamable.of(parts));
    }

    public static Name of(Streamable<String> parts) {
        return of(parts.toArray(String[]::new));
    }

    private final long length;
    private final Streamable<String> parts;

    private Name(Streamable<String> parts, long length) {
        this.parts = parts;
        this.length = length;
    }

    public long length() {
        return length;
    }

    public String[] toArray() {
        return parts.toArray(String[]::new);
    }

    public List<String> toList() {
        return parts.toList();
    }

    public String format(Collector<CharSequence, ?, String> collector, FormattingOption...options) {
        FormattingOption process = Stream.of(options).reduce(
                (s, i) -> s,
                (f1, f2) -> (s, i) -> f2.apply(f1.apply(s, i), i));
        return StreamUtils.zipWithIndex(parts.stream())
                .map(indexed -> process.apply(indexed.getValue(), indexed.getIndex()))
                .collect(collector);
    }

    public String toSeparated(FormattingOption...options) {
        return toSeparated(" ", options);
    }

    public String toSeparated(String separator, FormattingOption...options) {
        return format(Collectors.joining(separator), options);
    }

    public String toUnderscored() {
        return toUnderscored(FormattingOptions.LOWERCASE);
    }

    public String toUnderscored(FormattingOption...options) {
        return toSeparated("_", options);
    }

    public String toHyphenated() {
        return toHyphenated(FormattingOptions.LOWERCASE);
    }

    public String toHyphenated(FormattingOption...options) {
        return toSeparated("-", options);
    }

    public String toConstant() {
        return toSeparated("_", FormattingOptions.UPPERCASE);
    }

    public String toCamelCase() {
        return toCamelCase(true);
    }

    public String toCamelCase(boolean uppercaseAcronyms) {
        return uppercaseAcronyms
                ? format(Collectors.joining(), FormattingOptions.CAPITALISE_ALL_BUT_FIRST)
                : format(Collectors.joining(), FormattingOptions.LOWERCASE, FormattingOptions.CAPITALISE_ALL_BUT_FIRST);
    }

    public String toTitleCase() {
        return toTitleCase(true);
    }

    public String toTitleCase(boolean uppercaseAcronyms) {
        return uppercaseAcronyms
                ? format(Collectors.joining(), FormattingOptions.CAPITALISE_ALL)
                : format(Collectors.joining(), FormattingOptions.LOWERCASE, FormattingOptions.CAPITALISE_ALL);
    }

    public String toAddress() {
        return toSeparated(" ", FormattingOptions.CAPITALISE_ALL);
    }

    public Name concat(Name next) {
        return new Name(parts.concat(next.parts), length + next.length);
    }

    public Name withPrefix(String prefix) {
        return withPrefix(Name.of(prefix));
    }

    public Name withPrefix(Name prefix) {
        return prefix.concat(this);
    }

    public Name withSuffix(String suffix) {
        return withSuffix(Name.of(suffix));
    }

    public Name withSuffix(Name suffix) {
        return concat(suffix);
    }

    public Name transform(UnaryOperator<Stream<String>> transformer) {
        return of(parts.transform(transformer));
    }

    public Name map(UnaryOperator<String> f) {
        return new Name(parts.map(f), length);
    }

    public Name filter(Predicate<String> p) {
        return transform(s -> s.filter(p));
    }

    public <T> T collect(Collector<String, ?, T> collector) {
        return parts.collect(collector);
    }

    public Name uppercasing(String... termsToUppercase) {
        return uppercasingSet(Stream.of(termsToUppercase).map(String::toLowerCase).collect(Collectors.toSet()));
    }

    public Name uppercasing(Collection<String> termsToUppercase) {
        return uppercasingSet(termsToUppercase.stream().map(String::toLowerCase).collect(Collectors.toSet()));
    }

    private Name uppercasingSet(Set<String> termsToUppercase) {
        return map(n -> termsToUppercase.contains(n.toLowerCase()) ? n.toUpperCase() : n);
    }

    public Name withoutFirst() {
        return new Name(parts.skip(1), Math.max(0, length - 1));
    }

    public Name withoutLast() {
        long newLength = Math.max(length - 1, 0);
        return new Name(parts.transform(s -> s.limit(newLength)), newLength);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(toArray());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Name other = (Name) obj;
        return Objects.deepEquals(this.toArray(), other.toArray());
    }

    @Override
    public String toString() {
        return format(Collectors.joining(",","[","]"));
    }
}
