package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.DomainObject;

import java.math.BigInteger;

public interface Mapper<T extends DomainObject>{
    T insert(T obj);
//    String insertSQL(T obj)
    T update(T obj);
    void delete(T obj);
    Boolean exists(T obj);
}