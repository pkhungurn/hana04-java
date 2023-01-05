package hana04.distrib.util;

import java.util.LinkedList;
import java.util.Queue;

public class ObjectPoolMonitor<T> {
    private Queue<T> pool = new LinkedList<>();

    public synchronized T fetch() {
        while (pool.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return pool.remove();
    }

    public synchronized void deposit(T object) {
        pool.add(object);
        notify();
    }

    public synchronized int getNumObject() {
        return pool.size();
    }
}

