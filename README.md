# navn
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
assertThat(name.toUnderscored(), equalTo("xml_to_csv_converter"));
assertThat(name.toConstant(), equalTo("XML_TO_CSV_CONVERTER"));
assertThat(name.toAddress(), equalTo("XML To CSV Converter"));

assertThat(Name.of("foo_bar_baz").tocamelCase(), equalTo("fooBarBaz"));
assertThat(Name.of("std::io", ':').toHyphenated(FormattingOptions.UPPERCASE),
    equalTo("STD-IO"));

assertThat(Name.of("xml_to_csv_converter").uppercasing("csv").toCamelCase(),
        equalTo("xmlToCSVConverter"));

assertThat(Name.of("getDateOfBirth").withoutFirst().toUnderscored(),
        equalTo("date_of_birth"));
assertThat(Name.of("size_of_head").withPrefix("get").toCamelCase(),
        equalTo("getSizeOfHead"));
```
