package com.brogrammerbrigade.backend.port.postgres;

import com.brogrammerbrigade.backend.domain.DomainObject;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

// Lock Manager with wait implementation
public class LockManager {

    private static LockManager instance = new LockManager();

    /**
     * Stores the different lock managers for the respective domain objects.
     * It is implemented in this fashion as we are not using globally unique keys.
     * 
     *  First hashmap
     *  key: DomainObject Class Type
     *  value: lock manager for the particular domain object table
     *
     *  Lock Manager
     *  key: lockable (row id)
     *  value: LockInfo (Owner, Timeout time)
     */
    private ConcurrentHashMap<Class<? extends DomainObject>, ConcurrentHashMap<String, LockInfo>> masterLockMap;

    public LockManager() {
        masterLockMap = new ConcurrentHashMap<>();
    }

    public static synchronized LockManager getInstance(){
        if (instance == null){
            instance = new LockManager();
        }
        return instance;
    }

    /**
     *
     * @param domainObject
     * @param lockable
     * @param owner
     * @throws RuntimeException if resource cannot be obtained within time
     */
    public synchronized void acquireLock(DomainObject domainObject, String lockable, String owner) {
        ConcurrentHashMap<String, LockInfo> classLockManager = masterLockMap.computeIfAbsent(domainObject.getClass(), k -> new ConcurrentHashMap<>());
        LockInfo currentLock = classLockManager.get(lockable);
        if (currentLock == null) {
            classLockManager.put(lockable, new LockInfo(owner));
            System.out.println(owner + " acquired lock " + lockable + " of class type " + domainObject.getClass());
            return;
        } else if (currentLock.isExpired()){
            classLockManager.remove(lockable);
            classLockManager.put(lockable, new LockInfo(owner));
        }  else if (currentLock.getOwner().equals(owner)) {
            // Owner already has the lock
            throw new RuntimeException("Trying to acquire lock " + lockable + " when owner already has it");
        }


        System.out.println("Failed to acquire lock");
        throw new RuntimeException("Concurrency exception, " + owner + " could not acquire lock for " + lockable);
    }

    public synchronized boolean releaseLock(DomainObject domainObject, String lockable, String owner) {
        ConcurrentHashMap<String, LockInfo> classLockManager = masterLockMap.get(domainObject.getClass());
        if (classLockManager == null) {
            System.out.println("No lock manager found for class: " + domainObject.getClass().getSimpleName());
            return false;
        }

        LockInfo currentLock = classLockManager.get(lockable);
        if (currentLock == null) {
            System.out.println("No lock found for lockable: " + lockable);
            return false;
        }

        if (!owner.equals(currentLock.getOwner())) {
            System.out.println("Lock owner mismatch. Current owner: " + currentLock.getOwner() + ", Releasing owner: " + owner);
            return false;
        }
        System.out.println("Lock released successfully");
        classLockManager.remove(lockable);
        notifyAll();
        return true;
    }

    // Functions to check if lock is valid
    public synchronized void isLockValid(DomainObject domainObject, String lockable, String owner) {
        ConcurrentHashMap<String, LockInfo> classLockManager = masterLockMap.get(domainObject.getClass());
        if (classLockManager == null) {
            throw new RuntimeException("No owner found for resource");
        };

        LockInfo lockInfo = classLockManager.get(lockable);
        if (lockInfo == null) {
            throw new RuntimeException("No owner found for resource");
        }
        if (!owner.equals(lockInfo.getOwner())) {
            throw new RuntimeException("No write access for user");
        }
        if (lockInfo.isExpired()) {
            throw new RuntimeException("Resource lock expired");
        }
    }

}
