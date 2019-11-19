package com.bzdnet.demo.df.core;

import com.bzdnet.demo.df.dao.BaseDao;
import org.apache.commons.collections.map.HashedMap;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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

    public DaoRegistry(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void addDao(Class dao) {
        Object proxy = Proxy.newProxyInstance(dao.getClassLoader(), new Class[]{dao}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(this, args);
                }
                switch (method.getName()) {
                    case "allList":

                        break;
                }
                Connection conn = dataSource.getConnection();
                Statement statement = conn.createStatement();

                return null;
            }
        });
        daoMap.put(dao, proxy);
    }

    public void addDaoInPackage(String pkgName) {
        Reflections reflections = new Reflections(pkgName, new SubTypesScanner());
        Set<Class<? extends BaseDao>> daoSet = reflections.getSubTypesOf(BaseDao.class);
        for (Class clazz : daoSet) {
            addDao(clazz);
        }
    }

    public <T extends BaseDao> T getDao(Class<T> dao) {
        return (T) daoMap.get(dao);
    }


}
