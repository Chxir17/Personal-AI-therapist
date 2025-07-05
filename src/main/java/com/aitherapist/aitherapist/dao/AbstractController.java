package com.aitherapist.aitherapist.dao;

import java.util.List;

public abstract class AbstractController <E, K> {
    public abstract List<E> getAll();
    public abstract E getEntityById(K id);
    public abstract E getEntityByName(String name);
    public abstract E getEntityByChatId(long chatId);
    public abstract E update(E entity);
    public abstract boolean delete(E entity);
    public abstract boolean create(E entity);
    public abstract boolean find(E entity);
}
