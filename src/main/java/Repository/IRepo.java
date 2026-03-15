package main.java.Repository;

import Domain.Entity;

import java.util.List;

public interface IRepo <T extends Entity> {
    public void add(T t);
    public void remove(int id);
    public void update(T elem);
    public T findById(int id);
    public List<T> getAll();
    public int size();
}
