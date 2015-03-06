# navn
Name munging for Java (camel case to underscore, etc)

```java
Name name = Name.of("XML to CSV converter");

assertThat(name.toCamelCase(), equalTo("XMLToCSVConverter"));
assertThat(name.toUnderscored(), equalTo("xml_to_csv_converter"));
assertThat(name.toConstant(), equalTo("XML_TO_CSV_CONVERTER"));
assertThat(name.toAddress(), equalTo("XML To CSV Converter"));

assertThat(Name.of("foo_bar_baz").toLowerCamelCase(), equalTo("fooBarBaz"));
assertThat(Name.of("std::io", ':').toHyphenated(FormattingOptions.UPPERCASE),
    equalTo("STD-IO"));
```
