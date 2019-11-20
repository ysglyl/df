package com.bzdnet.demo.df.core;

import com.bzdnet.demo.df.annotation.DbColumn;
import com.bzdnet.demo.df.annotation.DbID;
import com.bzdnet.demo.df.annotation.DbRelation;
import com.bzdnet.demo.df.annotation.DbTable;
import com.bzdnet.demo.df.dao.BaseDao;
import org.apache.commons.collections.map.HashedMap;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

/**
 * 注册Dao层接口
 */
public class DaoRegistry {

    Map<Class<? extends BaseDao>, Object> daoMap = new HashedMap();
    JdbcTemplate jdbcTemplate;

    public DaoRegistry() {
    }

    public DaoRegistry(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addDao(Class dao) {
        Type[] types = dao.getGenericInterfaces();
        assert types.length == 1;
        ParameterizedType baseDao = (ParameterizedType) types[0];
        Type[] modelTypes = baseDao.getActualTypeArguments();
        assert modelTypes.length == 1;
        Class primaryModel = (Class) modelTypes[0];
        DbTable primaryTable = (DbTable) primaryModel.getDeclaredAnnotation(DbTable.class);
        String primaryTableName = primaryTable.table();
        Field[] fields = primaryModel.getDeclaredFields();
        List<String> selectColumns = new ArrayList<>();
        List<String> leftJoinTables = new ArrayList<>();
        ResultMap resultMap = new ResultMap();
        for (Field field : fields) {
            DbColumn primaryColumn = field.getDeclaredAnnotation(DbColumn.class);
            if (primaryColumn != null) {
                selectColumns.add(primaryTableName + "." + primaryColumn.column() + " " + primaryTableName + "_" + primaryColumn.column());
                List<ResultSimple> simpleList = resultMap.getSimpleList();
                if (simpleList == null) {
                    simpleList = new ArrayList<>();
                    resultMap.setSimpleList(simpleList);
                }
                simpleList.add(new ResultSimple(field.getName(), primaryTableName + "_" + primaryColumn.column(), field.getType()));
            } else {
                DbID primaryID = field.getDeclaredAnnotation(DbID.class);
                if (primaryID != null) {
                    selectColumns.add(primaryTableName + "." + primaryID.column() + " " + primaryTableName + "_" + primaryID.column());
                    assert resultMap.getResultID() == null;
                    resultMap.setResultID(new ResultID(field.getName(), primaryTableName + "_" + primaryID.column(), field.getType()));
                } else {
                    DbRelation dbRelation = field.getDeclaredAnnotation(DbRelation.class);
                    if (dbRelation != null) {
                        Class relationModel;
                        List<ResultAssociation> associationList = resultMap.getAssociationList();
                        List<ResultCollection> collectionList = resultMap.getCollectionList();
                        if (field.getType() == List.class) {
                            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                            relationModel = (Class) parameterizedType.getActualTypeArguments()[0];
                            if (collectionList == null) {
                                collectionList = new ArrayList<>();
                                resultMap.setCollectionList(collectionList);
                            }
                        } else {
                            relationModel = (Class) field.getGenericType();
                            if (associationList == null) {
                                associationList = new ArrayList<>();
                                resultMap.setAssociationList(associationList);
                            }
                        }
                        DbTable secondaryTable = (DbTable) relationModel.getDeclaredAnnotation(DbTable.class);
                        String secondaryTableName = secondaryTable.table();
                        Field[] secondaryFields = relationModel.getDeclaredFields();
                        ResultID resultID = null;
                        List<ResultSimple> simpleList = new ArrayList<>();
                        for (Field secondaryField : secondaryFields) {
                            DbColumn secondaryColumn = secondaryField.getDeclaredAnnotation(DbColumn.class);
                            if (secondaryColumn != null) {
                                selectColumns.add(secondaryTableName + "." + secondaryColumn.column() + " " + secondaryTableName + "_" + secondaryColumn.column());
                                simpleList.add(new ResultSimple(secondaryField.getName(), secondaryTableName + "_" + secondaryColumn.column(), secondaryField.getType()));
                            } else {
                                DbID secondaryID = secondaryField.getDeclaredAnnotation(DbID.class);
                                if (secondaryID != null) {
                                    resultID = new ResultID(secondaryField.getName(), secondaryTableName + "_" + secondaryID.column(), secondaryField.getType());
                                    selectColumns.add(secondaryTableName + "." + secondaryID.column() + " " + secondaryTableName + "_" + secondaryID.column());
                                }
                            }
                        }
                        switch (dbRelation.type()) {
                            case One2One:
                                leftJoinTables.add("left join " + secondaryTableName + " on " + primaryTableName + "." + dbRelation.primary() + "=" + secondaryTableName + "." + dbRelation.secondary());
                                associationList.add(new ResultAssociation(field.getName(), relationModel, resultID, simpleList));
                                break;
                            case One2Many:
                                leftJoinTables.add("left join " + secondaryTableName + " on " + primaryTableName + "." + dbRelation.primary() + "=" + secondaryTableName + "." + dbRelation.secondary());
                                collectionList.add(new ResultCollection(field.getName(), relationModel, resultID, simpleList));
                                break;
                            case Many2Many:
                                leftJoinTables.add("left join " + dbRelation.refTable() + " on " + primaryTableName + "." + dbRelation.primary() + "=" + dbRelation.refTable() + "." + dbRelation.refPrimary());
                                leftJoinTables.add("left join " + secondaryTableName + " on " + secondaryTableName + "." + dbRelation.secondary() + "=" + dbRelation.refTable() + "." + dbRelation.refSecondary());
                                collectionList.add(new ResultCollection(field.getName(), relationModel, resultID, simpleList));
                                break;
                        }
                    }
                }
            }
        }
        Object proxy = Proxy.newProxyInstance(dao.getClassLoader(), new Class[]{dao}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(this, args);
                }
                switch (method.getName()) {
                    case "allList":
                        String sql = "select " + String.join(",", selectColumns) + " from " + primaryTableName + " " + String.join(" ", leftJoinTables);
                        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
                        Map<String, Object> cache = new HashedMap();
                        List<Object> resultList = new ArrayList<>();
                        for (Map<String, Object> map : list) {
                            ResultID resultID = resultMap.getResultID();
                            List<ResultSimple> simpleList = resultMap.getSimpleList();
                            List<ResultAssociation> associationList = resultMap.getAssociationList();
                            List<ResultCollection> collectionList = resultMap.getCollectionList();
                            Object id = map.get(resultID.getColumn());
                            Object result = cache.get(String.valueOf(id));
                            if (result == null) {
                                result = primaryModel.newInstance();
                                cache.put(String.valueOf(id), result);
                                Method idMethod = primaryModel.getDeclaredMethod("set" + resultID.getProperty().substring(0, 1).toUpperCase() + resultID.getProperty().substring(1), resultID.getJavaType());
                                idMethod.invoke(result, id);
                                if (simpleList != null) {
                                    for (ResultSimple simple : simpleList) {
                                        Method columnMethod = primaryModel.getDeclaredMethod("set" + simple.getProperty().substring(0, 1).toUpperCase() + simple.getProperty().substring(1), simple.getJavaType());
                                        columnMethod.invoke(result, map.get(simple.getColumn()));
                                    }
                                }
                                resultList.add(result);
                            }
                            if (associationList != null) {
                                for (ResultAssociation association : associationList) {
                                    ResultID associationResultID = association.getResultID();
                                    Object associationID = map.get(associationResultID.getColumn());
                                    if (associationID == null) {
                                        continue;
                                    }
                                    Object associationResult = cache.get(id + "__" + associationID);
                                    if (associationResult == null) {
                                        associationResult = association.getModelClass().newInstance();
                                        cache.put(id + "__" + associationID, associationResult);

                                        Method associationIdMethod = association.getModelClass().getDeclaredMethod("set" + associationResultID.getProperty().substring(0, 1).toUpperCase() + associationResultID.getProperty().substring(1), associationResultID.getJavaType());
                                        associationIdMethod.invoke(associationResult, associationID);
                                        if (association.getSimpleList() != null) {
                                            for (ResultSimple simple : association.getSimpleList()) {
                                                Method columnMethod = association.getModelClass().getDeclaredMethod("set" + simple.getProperty().substring(0, 1).toUpperCase() + simple.getProperty().substring(1), simple.getJavaType());
                                                columnMethod.invoke(associationResult, map.get(simple.getColumn()));
                                            }
                                        }

                                        Method associationMethod = primaryModel.getDeclaredMethod("set" + resultID.getProperty().substring(0, 1).toUpperCase() + resultID.getProperty().substring(1), association.getModelClass());
                                        associationMethod.invoke(result, associationResult);
                                    }
                                }
                            }

                        }
                        return resultList;
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
            addDao(clazz);
        }
    }

    public <T extends BaseDao> T getDao(Class<T> dao) {
        return (T) daoMap.get(dao);
    }


}
