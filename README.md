## Spring JDBC bare

A standalone version of Spring JDBC module with no further dependencies on Spring Framework.
Primary motivation was to bring `JDBCTemplate` to Java EE environment with smaller footprint -- 430 K instead
of 5M. The only dependency left is commons-logging (which you likely want to exclude as well and replace with
`jcl-over-slf4j`).

The project is based on [branch 3.1.x](https://github.com/SpringSource/spring-framework/tree/3.1.x) of Spring
Framework, with some base classes moved into the module, and some of the features 
[removed](https://github.com/jdbc-bare/spring-jdbc-bare/tree/master/org.springframework.jdbc/src/removed/java/org/springframework/jdbc).

### Limitations

* No Resource transaction support. Users are expected to use container-managed datasource and transactions.
* No BeanPropertyRowMapper, BeanPropertySqlParameterSource or any other classes that were too dependant on
  other modules.
* SQL Custom Exception Mapping support is quite basic. Instead of full bean container there is simple implementation
  based on reflection and JAXB.

## Maven coordinates

Library is available on Maven Central:

```xml
<dependency>
  <groupId>com.github.jdbc-bare</groupId>
  <artifactId>spring-jdbc-bare</artifactId>
  <version>3.1.4.1</version>
</dependency>
```


## License
The project is released under version 2.0 of the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0).
