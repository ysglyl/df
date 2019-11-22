package com.bzdnet.demo.df.core;

import com.bzdnet.demo.df.core.annotation.DbColumn;
import com.bzdnet.demo.df.core.annotation.DbID;
import com.bzdnet.demo.df.core.annotation.DbRelation;
import com.bzdnet.demo.df.core.annotation.DbTable;
import com.bzdnet.demo.df.core.model.Condition;
import com.bzdnet.demo.df.core.model.KeyValuePair;
import com.bzdnet.demo.df.core.result.ResultMap;
import com.bzdnet.demo.df.core.result.ResultModel;
import com.bzdnet.demo.df.core.result.ResultValue;
import com.bzdnet.demo.df.core.wrapper.QueryWrapper;
import com.bzdnet.demo.df.core.wrapper.Wrapper;
import com.bzdnet.demo.df.dao.BaseDao;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.*;
import java.util.*;

/**
 * 注册Dao层接口
 */
public class DaoRegistry {

    private Map<Class, Object> daoMap = new HashMap<>();
    private Map<String, Method> methodCache = new HashMap<>();
    private Map<String, Object> tempCache = new HashMap<>();
    private JdbcTemplate jdbcTemplate;

    public DaoRegistry() {
    }

    public DaoRegistry(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addDaoInPackage(String pkgName) {
        Reflections reflections = new Reflections(pkgName, new SubTypesScanner());
        Set<Class<? extends BaseDao>> daoSet = reflections.getSubTypesOf(BaseDao.class);
        for (Class clazz : daoSet) {
            addDao(clazz);
        }
    }

    private void addDao(Class dao) {
        Type[] types = dao.getGenericInterfaces();
        ParameterizedType baseDao = (ParameterizedType) types[0];
        Type[] modelTypes = baseDao.getActualTypeArguments();
        Class primaryModel = (Class) modelTypes[0];
        ResultMap resultMap = getResultMapFromModel(primaryModel);
        Object proxy = Proxy.newProxyInstance(dao.getClassLoader(), new Class[]{dao}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(this, args);
                }
                switch (method.getName()) {
                    case "allList":
                        return getModelFromResultList(resultMap, queryList(resultMap, args));
                    case "pageList":

                        return null;
                    case "detailById":

                        return null;
                    case "insert":

                        return 0;
                    case "insertBatch":

                        return 0;
                    case "updateById":

                        return 0;
                    case "updateBatchById":

                        return 0;
                    case "update":

                        return 0;
                    case "updateBatch":

                        return 0;
                    case "deleteById":

                        return 0;
                    case "delete":

                        return 0;
                }
                return null;
            }
        });
        daoMap.put(dao, proxy);
    }

    public <T> T getDao(Class dao) {
        return (T) daoMap.get(dao);
    }

    /**
     * 获取缓存方法
     *
     * @param clazz
     * @param name
     * @param args
     * @return
     */
    private Method getCachedMethod(Class clazz, String name, Class... args) {
        try {
            Method method = methodCache.get(clazz.getName() + ".set" + name.substring(0, 1).toUpperCase() + name.substring(1));
            if (method == null) {
                method = clazz.getDeclaredMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), args);
                methodCache.put(clazz.getName() + ".set" + name.substring(0, 1).toUpperCase() + name.substring(1), method);
            }
            return method;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getSelectSql(String fromTable, List<String> leftJoinTables, List<String> selectColumns) {
        StringBuilder sb = new StringBuilder("select ");
        sb.append(String.join(",", selectColumns));
        sb.append(" from ");
        sb.append(fromTable);
        sb.append(" ");
        sb.append(String.join(" ", leftJoinTables));
        return sb.toString();
    }

    private String getCountSql(String fromTable, List<String> leftJoinTables) {
        StringBuilder sb = new StringBuilder("select count(1) from ");
        sb.append(fromTable);
        sb.append(" ");
        sb.append(String.join(" ", leftJoinTables));
        return sb.toString();
    }

    private String getLimitSql(long page, long pageSize) {
        long offset = (page - 1) * pageSize;
        return "limit " + offset + " " + pageSize;
    }

    private String getWhereSql(Wrapper wrapper) {
        StringBuilder sb = new StringBuilder();
        List<String> whereList = new ArrayList<>();
        List<Condition> conditions = wrapper.getConditions();
        List<Wrapper> ands = wrapper.getAnds();
        List<Wrapper> ors = wrapper.getOrs();
        for (Condition condition : conditions) {
            whereList.add(condition.getTable() + "." + condition.getColumn() + condition.getOperator().getOperator().replace("{}", String.valueOf(condition.getValue())));
        }
        sb.append(String.join(" and ", whereList));
        whereList.clear();
        for (Wrapper and : ands) {
            List<Condition> andCondition = and.getConditions();
            if (andCondition.size() > 0) {
                sb.append(" and ");
                for (Condition condition : andCondition) {
                    whereList.add(condition.getTable() + "." + condition.getColumn() + condition.getOperator().getOperator().replace("{}", String.valueOf(condition.getValue())));
                }
                sb.append("(");
                sb.append(String.join(" and ", whereList));
                sb.append(")");
                whereList.clear();
            }
        }
        for (Wrapper or : ors) {
            List<Condition> orCondition = or.getConditions();
            if (orCondition.size() > 0) {
                sb.append(" or ");
                for (Condition condition : orCondition) {
                    whereList.add(condition.getTable() + "." + condition.getColumn() + condition.getOperator().getOperator().replace("{}", String.valueOf(condition.getValue())));
                }
                sb.append("(");
                sb.append(String.join(" and ", whereList));
                sb.append(")");
                whereList.clear();
            }
        }
        return sb.toString().trim().length() > 0 ? "where " + sb.toString() : "";
    }

    private List<Map<String, Object>> queryList(ResultMap resultMap, Object[] args) {
        List<String> selectColumns = new ArrayList<>();
        List<String> leftJoinTables = new ArrayList<>();
        String primaryTableName = resultMap.getTableName();
        selectColumns.add(primaryTableName + "." + resultMap.getId().getColumn() + " " + primaryTableName + "_" + resultMap.getId().getColumn());
        for (ResultValue resultValue : resultMap.getColumns()) {
            selectColumns.add(primaryTableName + "." + resultValue.getColumn() + " " + primaryTableName + "_" + resultValue.getColumn());
        }
        for (ResultModel resultModel : resultMap.getAssociations()) {
            selectColumns.add(resultModel.getTableName() + "." + resultModel.getId().getColumn() + " " + resultModel.getTableName() + "_" + resultModel.getId().getColumn());
            for (ResultValue resultValue : resultModel.getColumns()) {
                selectColumns.add(resultModel.getTableName() + "." + resultValue.getColumn() + " " + resultModel.getTableName() + "_" + resultValue.getColumn());
            }
            leftJoinTables.add("left join " + resultModel.getTableName() + " on " + primaryTableName + "." + resultModel.getRelation().getKey() + "=" + resultModel.getTableName() + "." + resultModel.getRelation().getValue());
        }
        for (ResultModel resultModel : resultMap.getCollections()) {
            selectColumns.add(resultModel.getTableName() + "." + resultModel.getId().getColumn() + " " + resultModel.getTableName() + "_" + resultModel.getId().getColumn());
            for (ResultValue resultValue : resultModel.getColumns()) {
                selectColumns.add(resultModel.getTableName() + "." + resultValue.getColumn() + " " + resultModel.getTableName() + "_" + resultValue.getColumn());
            }
            if (resultModel.getRelation() == null) { //如果为null则使用中间表作为关系
                leftJoinTables.add("left join " + resultModel.getRefTableName() + " on " + primaryTableName + "." + resultModel.getRefPrimary().getKey() + "=" + resultModel.getRefTableName() + "." + resultModel.getRefPrimary().getValue());
                leftJoinTables.add("left join " + resultModel.getTableName() + " on " + resultModel.getTableName() + "." + resultModel.getRefSecondary().getKey() + "=" + resultModel.getRefTableName() + "." + resultModel.getRefSecondary().getValue());
            } else {
                leftJoinTables.add("left join " + resultModel.getTableName() + " on " + primaryTableName + "." + resultModel.getRelation().getKey() + "=" + resultModel.getTableName() + "." + resultModel.getRelation().getValue());
            }
        }
        String sql = "select " + String.join(",", selectColumns) + " from " + primaryTableName + " " + String.join(" ", leftJoinTables);
        if (args != null) {
            QueryWrapper queryWrapper = (QueryWrapper) args[0];
            sql += " " + getWhereSql(queryWrapper);
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }


    /**
     * 根据实体类型，生产映射关系
     *
     * @param model
     * @return
     */
    private ResultMap getResultMapFromModel(Class model) {
        // 获取主表名称
        DbTable primaryTable = (DbTable) model.getDeclaredAnnotation(DbTable.class);
        String primaryTableName = primaryTable.table();
        ResultMap resultMap = new ResultMap();
        resultMap.setTableName(primaryTableName);
        resultMap.setType(model);

        Field[] primaryFields = model.getDeclaredFields();
        for (Field primaryField : primaryFields) {
            DbColumn primaryColumn = primaryField.getDeclaredAnnotation(DbColumn.class);
            if (primaryColumn != null) {
                List<ResultValue> primaryColumns = resultMap.getColumns();
                if (primaryColumns == null) {
                    primaryColumns = new ArrayList<>();
                    resultMap.setColumns(primaryColumns);
                }
                primaryColumns.add(new ResultValue(primaryField.getName(), primaryColumn.column(), primaryField.getType()));
            } else {
                DbID primaryId = primaryField.getDeclaredAnnotation(DbID.class);
                if (primaryId != null) {
                    resultMap.setId(new ResultValue(primaryField.getName(), primaryId.column(), primaryField.getType()));
                } else {
                    DbRelation primaryRelation = primaryField.getDeclaredAnnotation(DbRelation.class);
                    if (primaryRelation != null) {
                        Class relationModel;
                        List<ResultModel> associations = resultMap.getAssociations();
                        List<ResultModel> collections = resultMap.getCollections();
                        if (primaryField.getType() == List.class) {
                            ParameterizedType parameterizedType = (ParameterizedType) primaryField.getGenericType();
                            relationModel = (Class) parameterizedType.getActualTypeArguments()[0];
                            if (collections == null) {
                                collections = new ArrayList<>();
                                resultMap.setCollections(collections);
                            }
                        } else {
                            relationModel = (Class) primaryField.getGenericType();
                            if (associations == null) {
                                associations = new ArrayList<>();
                                resultMap.setAssociations(associations);
                            }
                        }
                        DbTable secondaryTable = (DbTable) relationModel.getDeclaredAnnotation(DbTable.class);
                        String secondaryTableName = secondaryTable.table();
                        Field[] secondaryFields = relationModel.getDeclaredFields();
                        ResultValue secondaryId = null;
                        List<ResultValue> secondaryColumns = new ArrayList<>();
                        for (Field secondaryField : secondaryFields) {
                            DbColumn secondaryColumn = secondaryField.getDeclaredAnnotation(DbColumn.class);
                            if (secondaryColumn != null) {
                                secondaryColumns.add(new ResultValue(secondaryField.getName(), secondaryColumn.column(), secondaryField.getType()));
                            } else {
                                DbID secondaryIdA = secondaryField.getDeclaredAnnotation(DbID.class);
                                if (secondaryIdA != null) {
                                    secondaryId = new ResultValue(secondaryField.getName(), secondaryIdA.column(), secondaryField.getType());
                                }
                            }
                        }
                        switch (primaryRelation.type()) {
                            case One2One:
                                associations.add(new ResultModel(
                                        primaryField.getName(),
                                        relationModel,
                                        secondaryTableName,
                                        secondaryId,
                                        secondaryColumns,
                                        new KeyValuePair(primaryRelation.primary(), primaryRelation.secondary())
                                ));
                                break;
                            case One2Many:
                                collections.add(new ResultModel(
                                        primaryField.getName(),
                                        relationModel,
                                        secondaryTableName,
                                        secondaryId,
                                        secondaryColumns,
                                        new KeyValuePair(primaryRelation.primary(), primaryRelation.secondary())
                                ));
                                break;
                            case Many2Many:
                                collections.add(new ResultModel(
                                        primaryField.getName(),
                                        relationModel,
                                        secondaryTableName,
                                        secondaryId,
                                        secondaryColumns,
                                        primaryRelation.refTable(),
                                        new KeyValuePair(primaryRelation.primary(), primaryRelation.refPrimary()),
                                        new KeyValuePair(primaryRelation.secondary(), primaryRelation.refSecondary())
                                ));
                                break;
                        }
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * 将结果集转换成Model
     *
     * @param resultMap
     * @param list
     * @return
     * @throws Exception
     */
    private List<Object> getModelFromResultList(ResultMap resultMap, List<Map<String, Object>> list) throws Exception {
        List<Object> resultList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            String table = resultMap.getTableName();
            ResultValue id = resultMap.getId();
            List<ResultValue> columns = resultMap.getColumns();
            List<ResultModel> associations = resultMap.getAssociations();
            List<ResultModel> collections = resultMap.getCollections();
            Object idValue = map.get(table + "_" + id.getColumn());
            Object result = tempCache.get(String.valueOf(idValue));
            if (result == null) {
                result = resultMap.getType().newInstance();
                tempCache.put(String.valueOf(idValue), result);
                Method idMethod = getCachedMethod(resultMap.getType(), id.getProperty(), id.getJavaType());
                idMethod.invoke(result, idValue);
                if (columns != null) {
                    for (ResultValue column : columns) {
                        Method columnMethod = getCachedMethod(resultMap.getType(), column.getProperty(), column.getJavaType());
                        columnMethod.invoke(result, map.get(table + "_" + column.getColumn()));
                    }
                }
                resultList.add(result);
            }
            if (associations != null) {
                for (ResultModel association : associations) {
                    String associationTable = association.getTableName();
                    ResultValue associationId = association.getId();
                    Object associationIdValue = map.get(associationTable + "_" + associationId.getColumn());
                    if (associationIdValue == null) {
                        continue;
                    }
                    Object associationModel = tempCache.get(idValue + "_A_" + associationTable + "_" + associationId.getColumn());
                    if (associationModel == null) {
                        associationModel = association.getModelClass().newInstance();
                        tempCache.put(idValue + "_A_" + associationTable + "_" + associationId.getColumn(), associationModel);

                        Method associationIdMethod = getCachedMethod(association.getModelClass(), associationId.getProperty(), associationId.getJavaType());
                        associationIdMethod.invoke(associationModel, associationIdValue);
                        if (association.getColumns() != null) {
                            for (ResultValue column : association.getColumns()) {
                                Method columnMethod = getCachedMethod(association.getModelClass(), column.getProperty(), column.getJavaType());
                                columnMethod.invoke(associationModel, map.get(associationTable + "_" + column.getColumn()));
                            }
                        }
                        Method associationMethod = getCachedMethod(resultMap.getType(), association.getProperty(), association.getModelClass());
                        associationMethod.invoke(result, associationModel);
                    }
                }
            }
            if (collections != null) {
                for (ResultModel collection : collections) {
                    String collectionTable = collection.getTableName();
                    ResultValue collectionId = collection.getId();
                    Object collectionIdValue = map.get(collectionTable + "_" + collectionId.getColumn());
                    if (collectionIdValue == null) {
                        continue;
                    }
                    List collectionModelList = (ArrayList) tempCache.get(idValue + "_L_" + collectionTable + "_" + collectionId.getColumn());
                    if (collectionModelList == null) {
                        collectionModelList = new ArrayList();
                        tempCache.put(idValue + "_L_" + collectionTable + "_" + collectionId.getColumn(), collectionModelList);

                        Method collectionMethod = getCachedMethod(resultMap.getType(), collection.getProperty(), List.class);
                        collectionMethod.invoke(result, collectionModelList);
                    }
                    if (!tempCache.containsKey(idValue + "_L_" + collectionTable + "_" + collectionId.getColumn() + "_" + collectionIdValue)) {
                        tempCache.put(idValue + "_L_" + collectionTable + "_" + collectionId.getColumn() + "_" + collectionIdValue, null);
                        Object collectionModel = collection.getModelClass().newInstance();
                        Method collectionIdMethod = getCachedMethod(collection.getModelClass(), collectionId.getProperty(), collectionId.getJavaType());
                        collectionIdMethod.invoke(collectionModel, collectionIdValue);
                        if (collection.getColumns() != null) {
                            for (ResultValue column : collection.getColumns()) {
                                Method columnMethod = getCachedMethod(collection.getModelClass(), column.getProperty(), column.getJavaType());
                                columnMethod.invoke(collectionModel, map.get(collectionTable + "_" + column.getColumn()));
                            }
                        }
                        collectionModelList.add(collectionModel);
                    }
                }
            }
        }
        return resultList;
    }

}
