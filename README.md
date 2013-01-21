## Spring JDBC bare
A standalone version of JDBC bare with no further dependencies on Spring framework. Primary motivation was to
bring JDBCTemplate to Java EE environment without small footprint.

### Limitations

* No Resource transaction support. Users are expected to use container-managed datasource and transactions.
* No BeanPropertyRowMapper. This feature requires too much of spring bean implentation.
* SQL Custom Exception Mapping support is quite basic, as project uses very simple JAXB based implementation of
  XML bean definition 


## License
The project is released under version 2.0 of the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0).