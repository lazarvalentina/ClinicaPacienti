package main.java.Repository;

import Exceptions.RepositoryException;

import java.util.ArrayList;
import java.util.List;

public class Repository<T> {

    protected List<T> entities = new ArrayList<>();

    public void clear(){
        entities.clear(); // numele listei tale interne
    }
    public void add(T elem) throws RepositoryException {
        entities.add(elem);
    }

    public List<T> getAll() {
        return entities;
    }

    public void remove(int id) throws RepositoryException {
        T found = entities.stream()
                .filter(e -> e instanceof Domain.Entity && ((Domain.Entity)e).getId() == id)
                .findFirst()
                .orElse(null);

        if(found == null)
            throw new RepositoryException("Nu exista element cu id=" + id);

        entities.remove(found);
    }

    public void update(T newElem) throws RepositoryException {
        int id = ((Domain.Entity)newElem).getId();
        remove(id);
        entities.add(newElem);
    }

    public int size() {
        return entities.size();
    }
}
