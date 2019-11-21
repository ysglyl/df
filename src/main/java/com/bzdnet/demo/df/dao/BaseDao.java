package com.bzdnet.demo.df.dao;

import com.bzdnet.demo.df.core.wrapper.QueryWrapper;
import com.bzdnet.demo.df.core.wrapper.Wrapper;
import com.bzdnet.demo.df.form.PagedForm;
import com.bzdnet.demo.df.result.PagedList;

import java.util.List;

public interface BaseDao<T> {

    List<T> allList();

    List<T> allList(QueryWrapper wrapper);

    PagedList<T> pageList(PagedForm form);

    PagedList<T> pageList(PagedForm form, Wrapper wrapper);

    T detailById(Long id);

    int insert(T t);

    int insertBatch(List<T> list);

    int updateById(T t);

    int updateBatchById(List<T> list);

    int update(T t, Wrapper wrapper);

    int updateBatch(List<T> list, Wrapper wrapper);

    int deleteById(Long id);

    int delete(Wrapper wrapper);

}
