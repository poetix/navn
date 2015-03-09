# navn

[![Build Status](https://travis-ci.org/poetix/navn.svg?branch=master)](https://travis-ci.org/poetix/navn)

Name munging for Java 8 (camel case to underscore, etc)

In maven:
```xml
<dependency>
    <groupId>com.codepoetics</groupId>
    <artifactId>navn</artifactId>
    <version>0.1</version>
</dependency>
```     

Example usage:
```java
Name name = Name.of("XML to CSV converter");

assertThat(name.toTitleCase(), equalTo("XMLToCSVConverter"));

// Don't uppercase acronyms
assertThat(name.toTitleCase(false), equalTo("XmlToCsvConverter"));

assertThat(name.toUnderscored(), equalTo("xml_to_csv_converter"));
assertThat(name.toConstant(), equalTo("XML_TO_CSV_CONVERTER"));
assertThat(name.toAddress(), equalTo("XML To CSV Converter"));

assertThat(Name.of("foo_bar_baz").toCamelCase(), equalTo("fooBarBaz"));
assertThat(Name.of("std::io", ':').toHyphenated(FormattingOptions.UPPERCASE),
        equalTo("STD-IO"));

// Selective uppercasing
assertThat(Name.of("xml_to_csv_converter").uppercasing("csv").toCamelCase(),
        equalTo("xmlToCSVConverter"));

// Prefix munging
assertThat(Name.of("getDateOfBirth").withoutFirst().toUnderscored(),
        equalTo("date_of_birth"));
assertThat(Name.of("size_of_head").withPrefix("get").toCamelCase(),
        equalTo("getSizeOfHead"));

// Disambiguation
assertThat(Name.of("foo-bar_baz-xyzzy", SourceFormat.UNDERSCORE_SEPARATED).toSeparated(),
        equalTo("foo-bar baz-xyzzy"));
assertThat(Name.of("foo-bar_baz-xyzzy", SourceFormat.HYPHEN_SEPARATED).toSeparated(),
        equalTo("foo bar_baz xyzzy"));
assertThat(Name.of("foo-bar_baz-xyzzy", "[_-]+").toSeparated(),
        equalTo("foo bar baz xyzzy"));

// Double-barrelled
assertThat(Name.of("martina_topley-bird", SourceFormat.UNDERSCORE_SEPARATED)
        .map(s -> Name.of(s).toHyphenated(FormattingOptions.CAPITALISE_ALL))
        .toSeparated(),
        equalTo("Martina Topley-Bird"));
```
