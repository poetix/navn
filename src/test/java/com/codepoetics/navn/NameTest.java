package com.codepoetics.navn;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class NameTest {

    @Test public void
    empty() {
        assertThat(Name.of(" - ").toList(), Matchers.hasSize(0));
    }

    @Test public void
    readsWhitespaceSeparated() {
        assertThat(Name.of("Arthur Putey").toList(), contains("Arthur", "Putey"));
        assertThat(Name.of(" Arthur  Putey").toList(), contains("Arthur", "Putey"));
        assertThat(Name.of("\tArthur\nJ.\tPutey  ").toList(), contains("Arthur", "J.", "Putey"));
    }

    @Test public void
    readsUnderscoreSeparated() {
        assertThat(Name.of("arthur_putey").toList(), contains("arthur", "putey"));
        assertThat(Name.of("__arthur__putey_").toList(), contains("arthur", "putey"));
    }

    @Test public void
    readsHyphenSeparated() {
        assertThat(Name.of("arthur-putey").toList(), contains("arthur", "putey"));
        assertThat(Name.of("--arthur--putey-").toList(), contains("arthur", "putey"));
    }

    @Test public void
    readsCaseSeparated() {
        assertThat(Name.of("lowerCamelCase").toList(), contains("lower", "Camel", "Case"));
        assertThat(Name.of("UpperCamelCase").toList(), contains("Upper", "Camel", "Case"));
        assertThat(Name.of("CsvReader").toList(), contains("Csv", "Reader"));
        assertThat(Name.of("  CSVReader ").toList(), contains("CSV", "Reader"));
        assertThat(Name.of("WriteXMLToYAML").toList(), contains("Write", "XML", "To", "YAML"));
        assertThat(Name.of("NewXMLWriter").toList(), contains("New", "XML", "Writer"));
        assertThat(Name.of("CSVToXMLConverter").toList(), contains("CSV", "To", "XML", "Converter"));
    }

    @Test public void
    readsColonSeparated() {
        assertThat(Name.of("foo::bar::baz", ':').toList(), contains("foo", "bar", "baz"));
    }

    @Test public void
    writesUnderscoreSeparated() {
        assertThat(Name.of("Algernon~Charles~Swinburne", '~').toUnderscored(),
                equalTo("algernon_charles_swinburne"));
    }

    @Test public void
    writesHyphenSeparated() {
        assertThat(Name.of("Francois Laruelle").toHyphenated(),
                equalTo("francois-laruelle"));
    }

    @Test public void
    writesConstant() {
        assertThat(Name.of("Algernon~Charles~Swinburne", '~').toConstant(),
                equalTo("ALGERNON_CHARLES_SWINBURNE"));
    }

    @Test public void
    writesCamelCase() {
        assertThat(Name.of("Pretty printer").toTitleCase(),
                equalTo("PrettyPrinter"));
        assertThat(Name.of("CSV To XML Converter").toTitleCase(),
                equalTo("CSVToXMLConverter"));
    }

    @Test public void
    writesLowerCamelCase() {
        assertThat(Name.of("Pretty printer").toCamelCase(),
                equalTo("prettyPrinter"));
        assertThat(Name.of("CSV To XML Converter").toCamelCase(),
                equalTo("CSVToXMLConverter"));
    }

    @Test public void
    writesAddress() {
        assertThat(Name.of("arthur_hugh_clough").toAddress(),
                equalTo("Arthur Hugh Clough"));
    }

    @Test public void
    namesAreEqualIfTheyHaveTheSameParts() {
        assertThat(Name.of("arthur_hugh_clough"),
                equalTo(Name.of("arthur hugh clough")));
        assertThat(Name.of("arthur_hugh_clough").hashCode(),
                equalTo(Name.of("arthur hugh clough").hashCode()));
    }

    @Test public void
    namesHaveADefaultStringRepresentation() {
        assertThat(Name.of("charles_lutwidge_dodgson").toString(),
                equalTo("[charles,lutwidge,dodgson]"));
    }

    @Test public void
    namesCanBeGivenPrefixes() {
        assertThat(Name.of("Mark E. Smith").withPrefix("Mr").toAddress(),
                equalTo("Mr Mark E. Smith"));
        assertThat(Name.of("toXMLTransformer").withPrefix("fromCSV").toUnderscored(),
                equalTo("from_csv_to_xml_transformer"));
    }

    @Test public void
    namesCanBeGivenSuffixes() {
        assertThat(Name.of("robertDowney").withSuffix("Jr").toAddress(),
                equalTo("Robert Downey Jr"));
        assertThat(Name.of("KingRichard").withSuffix("the_third").toConstant(),
                equalTo("KING_RICHARD_THE_THIRD"));
    }

    @Test public void
    customFormatting() {
        FormattingOption yourNameInLights = (s, i) ->
                s.chars()
                .mapToObj(c -> Character.toUpperCase((char) c))
                .map(Object::toString)
                .collect(Collectors.joining("*"));

        assertThat(Name.of("arthur_putey").format(Collectors.joining(" "), yourNameInLights),
                equalTo("A*R*T*H*U*R P*U*T*E*Y"));
    }

    @Test public void
    uppercasing() {
        assertThat(Name.of("xml_to_csv_converter").toCamelCase(),
                equalTo("xmlToCsvConverter"));
        assertThat(Name.of("xml_to_csv_converter").uppercasing("xml").toCamelCase(),
                equalTo("XMLToCsvConverter"));
        assertThat(Name.of("xml_to_csv_converter").uppercasing("csv").toCamelCase(),
                equalTo("xmlToCSVConverter"));
        assertThat(Name.of("xml_to_csv_converter").uppercasing("xml", "csv").toCamelCase(),
                equalTo("XMLToCSVConverter"));
    }

    @Test public void
    droppingPrefix() {
        assertThat(Name.of("getDateOfBirth").withoutFirst().toUnderscored(),
                equalTo("date_of_birth"));
    }

    @Test public void
    droppingSuffix() {
        assertThat(Name.of("beanManagerWrapperFactory").withoutLast().toHyphenated(),
                equalTo("bean-manager-wrapper"));
    }

    @Test public void
    examplesForDocumentation() {
        Name name = Name.of("XML to CSV converter");

        assertThat(name.toTitleCase(), equalTo("XMLToCSVConverter"));
        assertThat(name.toUnderscored(), equalTo("xml_to_csv_converter"));
        assertThat(name.toConstant(), equalTo("XML_TO_CSV_CONVERTER"));
        assertThat(name.toAddress(), equalTo("XML To CSV Converter"));

        assertThat(Name.of("foo_bar_baz").toCamelCase(), equalTo("fooBarBaz"));
        assertThat(Name.of("std::io", ':').toHyphenated(FormattingOptions.UPPERCASE),
                equalTo("STD-IO"));

        assertThat(Name.of("xml_to_csv_converter").uppercasing("csv").toCamelCase(),
                equalTo("xmlToCSVConverter"));

        assertThat(Name.of("getDateOfBirth").withoutFirst().toUnderscored(),
                equalTo("date_of_birth"));
        assertThat(Name.of("size_of_head").withPrefix("get").toCamelCase(),
                equalTo("getSizeOfHead"));
    }

}
