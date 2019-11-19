package com.bzdnet.demo.df.core;

import com.bzdnet.demo.df.annotation.DbColumn;
import com.bzdnet.demo.df.annotation.DbID;
import com.bzdnet.demo.df.annotation.DbTable;
import com.bzdnet.demo.df.dao.BaseDao;
import org.apache.commons.collections.map.HashedMap;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.sql.DataSource;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

/**
 * 注册Dao层接口
 */
public class DaoRegistry {

    Map<Class<? extends BaseDao>, Object> daoMap = new HashedMap();
    DataSource dataSource;

    public DaoRegistry() {
    }

    public DaoRegistry(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addDao(Class dao) throws Exception {
        Type[] types = dao.getGenericInterfaces();
        assert types.length == 1;
        ParameterizedType baseDao = (ParameterizedType) types[0];
        Type[] modelTypes = baseDao.getActualTypeArguments();
        assert modelTypes.length == 1;
        Class modelClass = (Class) modelTypes[0];
        DbTable dbTable = (DbTable) modelClass.getDeclaredAnnotation(DbTable.class);
        String tableName = dbTable.table();
        Field[] fields = modelClass.getDeclaredFields();
        for (Field field : fields) {
            DbID dbID = field.getDeclaredAnnotation(DbID.class);
            if (dbID != null) {

            }
            DbColumn dbColumn = field.getDeclaredAnnotation(DbColumn.class);
            if (dbColumn != null) {

            }
        }
        Object proxy = Proxy.newProxyInstance(dao.getClassLoader(), new Class[]{dao}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(this, args);
                }
                assert dataSource != null;
                Connection conn = dataSource.getConnection();
                Statement statement = conn.createStatement();
                switch (method.getName()) {
                    case "allList":

                        break;
                }
                return null;
            }
        });
        daoMap.put(dao, proxy);
    }

    public void addDaoInPackage(String pkgName) {
        Reflections reflections = new Reflections(pkgName, new SubTypesScanner());
        Set<Class<? extends BaseDao>> daoSet = reflections.getSubTypesOf(BaseDao.class);
        for (Class clazz : daoSet) {
            try {
                addDao(clazz);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public <T extends BaseDao> T getDao(Class<T> dao) {
        return (T) daoMap.get(dao);
    }


}
