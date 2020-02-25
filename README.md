# 模块一--自定义MyBatis持久层框架

### auth:郑飞



#### 1.构造SqlSessionFactory

```
public SqlSessionFactory build(InputStream inputStream) throws PropertyVetoException, DocumentException {
    XMLCofingBuilder xmlCofingBuilder = new XMLCofingBuilder();
    Configuration configuration = xmlCofingBuilder.parseXml(inputStream);
    SqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);
    return defaultSqlSessionFactory;
}
```

##### a.解析核心配置文件

```
public Configuration parseXml(InputStream inputStream) throws DocumentException, PropertyVetoException {
    SAXReader saxReader = new SAXReader();
    Document document = saxReader.read(inputStream);
    Element rootElement = document.getRootElement();
    List<Element> list = rootElement.selectNodes("//property");
    Properties properties = new Properties();
    for(Element element:list){
        String name = element.attributeValue("name");
        String value = element.attributeValue("value");
        properties.setProperty(name,value);
    }
    ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
    comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
    comboPooledDataSource.setJdbcUrl(properties.getProperty("url"));
    comboPooledDataSource.setUser(properties.getProperty("username"));
    comboPooledDataSource.setPassword(properties.getProperty("password"));
    configuration.setDataSource(comboPooledDataSource);

    List<Element> mapperList = rootElement.selectNodes("//mapper");
    for(Element mapper:mapperList){
        String path = mapper.attributeValue("value");
        InputStream resourceAsStream = Resource.getResourceAsStream(path);
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
        xmlMapperBuilder.parseMapperXml(resourceAsStream);
    }
    return configuration;
}
```

##### b.解析mapper.xml文件

```
public void parseMapperXml(InputStream inputStream) throws DocumentException {
    Document document = new SAXReader().read(inputStream);
    Element rootElement = document.getRootElement();
    String namespace = rootElement.attributeValue("namespace");
    //解析select
    attrSelectNodes(namespace,rootElement);
    //解析insert
    attrInsertNodes(namespace,rootElement);
    //解析delete
    attrDeleteNodes(namespace,rootElement);
    //解析update
    attrUpdateNodes(namespace,rootElement);
}

private void attrUpdateNodes(String namespace, Element rootElement) {
    List<Element> list = rootElement.selectNodes("//update");
    operateNodes(namespace,list, SqlType.UPDATE);
}

private void attrDeleteNodes(String namespace, Element rootElement) {
    List<Element> list = rootElement.selectNodes("//delete");
    operateNodes(namespace,list, SqlType.DELETE);
}

private void attrInsertNodes(String namespace, Element rootElement) {
    List<Element> list = rootElement.selectNodes("//insert");
    operateNodes(namespace,list, SqlType.INSERT);
}

/**
 *  解析select node
 * @param rootElement
 */
private void attrSelectNodes(String namespace,Element rootElement) {
    List<Element> list = rootElement.selectNodes("//select");
    operateNodes(namespace,list, SqlType.SELECT);
}

private void operateNodes(String namespace,List<Element> list,SqlType sqlType){
    for(Element element:list){
        String id = element.attributeValue("id");
        String parameterType = element.attributeValue("parameterType");
        String resultType = element.attributeValue("resultType");
        String sql = element.getText();
        MappedStatement statement = new MappedStatement();
        statement.setId(id);
        statement.setParameterType(parameterType);
        statement.setResultType(resultType);
        statement.setSql(sql);
        statement.setSqlType(sqlType);
        String statmentId = namespace+"."+id;
        configuration.getMappedStatementMap().put(statmentId,statement);
    }
}
```

#### 2.采用通用方法getMapper方式实现CRUD

```
public <T> T getMapper(Class<?> clazz) {
        Object instance = Proxy.newProxyInstance(
        		DefaultSqlSession.class.getClassLoader(), 
        		new Class[]{clazz}, 
        		new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className+"."+methodName;

                Type genericReturnType = method.getGenericReturnType();
                if(genericReturnType instanceof ParameterizedType){
                    //若返回参数为泛型类型
                    List<Object> objects = selectList(statementId, args);
                    return objects;
                }
                return executeType(statementId,args);
//                return selectOne(statementId,args);
            }
        });
        return (T) instance;
    }
```

##### a.查询列表selectList(statementId, args);

```
    public <E> List<E> selectList(String statmentId, Object... args) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException {
//        Excutor simpleExcutor = new SimpleExcutor();
        MappedStatement statement = configuration.getMappedStatementMap().get(statmentId);
        List<Object> query = simpleExcutor.query(configuration, statement, args);
        return (List<E>) query;
    }
```

```
public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... args) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException {
    Connection connection = configuration.getDataSource().getConnection();
    String sql = mappedStatement.getSql();
    BoundSql boundSql = getBoundSql(sql);
    List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
    String parameterType = mappedStatement.getParameterType();
    String resultType = mappedStatement.getResultType();
    PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());
    //设置参数
    setParameters(preparedStatement,parameterMappingList,parameterType,args);

    ResultSet resultSet = preparedStatement.executeQuery();
    List<Object> list = new LinkedList<Object>();
    Class<?> resultClazz = getClazz(resultType);
    while (resultSet.next()){
        Object o = resultClazz.newInstance();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        for(int i = 1;i<=count;i++){
            String columnName = metaData.getColumnName(i);
            Object value = resultSet.getObject(columnName);
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName,resultClazz);
            Method writeMethod = propertyDescriptor.getWriteMethod();
            writeMethod.invoke(o,value);
        }
        list.add(o);
    }
    preparedStatement.close();
    connection.close();
    return (List<E>) list;
}
```

##### b.查询单个，新增，删除，更新

```
private <T> T executeType(String statmentId, Object... args) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException {
    Map<String, MappedStatement> mappedStatementMap = configuration.getMappedStatementMap();
    MappedStatement statement = mappedStatementMap.get(statmentId);
    Object o = null;
    switch (statement.getSqlType()){
        case DELETE:
            o = execute(configuration,statement,args);
            break;
        case INSERT:
            o = execute(configuration,statement,args);
            break;
        case UPDATE:
            o = execute(configuration,statement,args);
            break;
        case SELECT:
            o = selectOne(statmentId, args);
            break;
    }
    return (T) o;
}

private Integer execute(Configuration configuration,MappedStatement statement, Object... args) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException {
    return simpleExcutor.execute(configuration,statement,args);
}
```

```
public Integer execute(Configuration configuration, MappedStatement mappedStatement, Object... args) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException {
        Connection connection = configuration.getDataSource().getConnection();
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        String parameterType = mappedStatement.getParameterType();
        String resultType = mappedStatement.getResultType();
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());
        //设置参数
        setParameters(preparedStatement,parameterMappingList,parameterType,args);

        //执行insert update delete 操作，返回成功条数
        int update = preparedStatement.executeUpdate();

        //执行sql语句，返回true--失败  false--成功
//        boolean execute = preparedStatement.execute();
        preparedStatement.close();
        connection.close();
        return update;
    }
```

3.测试

```
@Test
    public void test2() throws Exception {
        InputStream resourceAsStream = Resource.getResourceAsStream("sqlMapperConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = build.openSqlSession();

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
       /*
       //测试更新
       User user1 = new User();
        user1.setId(4);
        user1.setUsername("zhaoliu2");
        Integer integer = userDao.updateById(user1);
        System.out.println(integer);*/
/*
        //测试删除
        User user2 = new User();
        user2.setId(4);
        Integer integer = userDao.deleteById(user2);
        System.out.println(integer);*/
        //测试新增
        User user3 = new User();
        user3.setId(5);
        user3.setUsername("testinsert");
        Integer integer = userDao.insert(user3);
        System.out.println(integer);

    }
```