package com.brogrammerbrigade.backend.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.brogrammerbrigade.backend.domain.DomainObject;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;
import org.springframework.util.Assert;

public class UnitOfWork {
    private static final ThreadLocal<UnitOfWork> current = new ThreadLocal<UnitOfWork>();

    private final List<DomainObject> newObjects = new ArrayList<DomainObject>();
    private final List<DomainObject> dirtyObjects = new ArrayList<DomainObject>();
    private final List<DomainObject> deletedObjects = new ArrayList<DomainObject>();

    public static void newCurrent() {
        setCurrent (new UnitOfWork());
        System.out.println("Thread created");
    }
    public static void setCurrent (UnitOfWork uow) {
        current.set(uow);
    }

    public static UnitOfWork getCurrent() {
        return (UnitOfWork) current.get();
    }

    public void registerNew(DomainObject obj) {
        Assert.isTrue(!dirtyObjects.contains(obj), "object is dirty");
        Assert.isTrue(!deletedObjects.contains(obj), "object is deleted");
        Assert.isTrue(!newObjects.contains(obj), "object is new");
        newObjects.add(obj);
    }

    public void registerDirty(DomainObject obj) {
        Assert.isTrue(!deletedObjects.contains(obj), "object is deleted");
        if (!dirtyObjects.contains(obj) && !newObjects.contains(obj)) {
            dirtyObjects.add(obj);
        }
    }

    public void registerDeleted(DomainObject obj) {
        if (newObjects.remove(obj)) return;
        dirtyObjects.remove(obj);
        if (!deletedObjects.contains(obj)) {
            deletedObjects.add(obj);
        }
    }

    @SuppressWarnings("unchecked")
    public void commit() {
        System.out.println("Unit of Work Commit");
        for (DomainObject obj : newObjects) {
            Mapper<DomainObject> mapper = (Mapper<DomainObject>) DataMapperFactory.getInstance().getMapper(obj.getClass());
            mapper.insert(obj);
        }
        for (DomainObject obj : dirtyObjects) {
            Mapper<DomainObject> mapper = (Mapper<DomainObject>) DataMapperFactory.getInstance().getMapper(obj.getClass());
            mapper.update(obj);

        }
        for (DomainObject obj : deletedObjects) {
            Mapper<DomainObject> mapper = (Mapper<DomainObject>) DataMapperFactory.getInstance().getMapper(obj.getClass());
            mapper.delete(obj);
        }
        clear();
    }

    // Clear all tracked entities after commit
    private void clear() {
        newObjects.clear();
        dirtyObjects.clear();
        deletedObjects.clear();
    }
}

