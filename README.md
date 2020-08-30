![Maven Central](https://img.shields.io/maven-central/v/site.kason/dbutil.svg)

database utilities writing in kalang

# Installation

### Gradle

    compile 'site.kason:dbutil:2.1.0'
    compile 'mysql:mysql-connector-java:6.0.3'
    
# Usage
    
### Normal query
    
    Class.forName("com.mysql.cj.jdbc.Driver");
    val conn = DBUtil.connect("jdbc:mysql://localhost:3306/test");
    val resutl = conn.queryOne("select * from tb where id=?", 1);
    println(result);
    conn.exec("update tb set name=? where id=?", "test", 1);

### Using SQL builder

    val result = conn.selectOne("tb", q => q.eq("id",1));
    println(result);
    conn.insert("tb", ["id":1,"name":"test"]);