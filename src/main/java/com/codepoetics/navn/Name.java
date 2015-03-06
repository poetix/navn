package com.codepoetics.navn;

import com.codepoetics.protonpack.StreamUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Name {

    public static Name of(String source) {
        String trimmed = source.trim();
        return Stream.of(SourceFormat.values())
                .filter(format -> format.test(trimmed))
                .map(format -> of(trimmed, format))
                .findFirst()
                .orElseGet(() -> of(new String[]{trimmed}));
    }

    public static Name of(String parts, char separator) {
        return of(parts, s -> s.split("\\" + separator + "+"));
    }

    public static Name of(String source, Function<String, String[]> reader) {
        return of(reader.apply(source));
    }

    public static Name of(String[] parts) {
        return new Name(parts);
    }

    private final String[] parts;
    private Name(String[] parts) {
        this.parts = parts;
    }

    public String[] toArray() {
        return Arrays.copyOf(parts, parts.length);
    }

    public List<String> toList() {
        return Arrays.asList(parts);
    }

    public String format(Collector<CharSequence, ?, String> collector, FormattingOption...options) {
        FormattingOption process = Stream.of(options).reduce(
                (s, i) -> s,
                (f1, f2) -> (s, i) -> f2.apply(f1.apply(s, i), i));
        return StreamUtils.zipWithIndex(Stream.of(parts))
                .map(indexed -> process.apply(indexed.getValue(), indexed.getIndex()))
                .collect(collector);
    }

    public String toSeparated(String separator, FormattingOption...options) {
        return format(Collectors.joining(separator), options);
    }

    public String toUnderscored() {
        return toSeparated("_", FormattingOptions.LOWERCASE);
    }

    public String toConstant() {
        return toSeparated("_", FormattingOptions.UPPERCASE);
    }

    public String toCamelCase() {
        return format(Collectors.joining(), FormattingOptions.CAPITALISE_ALL);
    }

    public String toLowerCamelCase() {
        return format(Collectors.joining(), FormattingOptions.CAPITALISE_ALL_BUT_FIRST);
    }

    public String toAddress() {
        return toSeparated(" ", FormattingOptions.CAPITALISE_ALL);
    }

    public Name withPrefix(String prefix) {
        return withPrefix(Name.of(prefix));
    }

    public Name withPrefix(Name prefix) {
        return transform(s -> Stream.concat(
                Stream.of(prefix.parts),
                s));
    }

    public Name withSuffix(String suffix) {
        return withSuffix(Name.of(suffix));
    }

    public Name withSuffix(Name suffix) {
        return transform(s -> Stream.concat(
                s,
                Stream.of(suffix.parts)));
    }

    public Name transform(UnaryOperator<Stream<String>> transformer) {
        return new Name(transformer.apply(Stream.of(parts)).toArray(String[]::new));
    }

    public Name map(UnaryOperator<String> f) {
        return transform(s -> s.map(f));
    }

    public Name filter(Predicate<String> p) {
        return transform(s -> s.filter(p));
    }

    public <T> T collect(Collector<String, ?, T> collector) {
        return Stream.of(parts).collect(collector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(parts);
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
        return Objects.deepEquals(this.parts, other.parts);
    }

    @Override
    public String toString() {
        return format(Collectors.joining(",","[","]"));
    }
}
