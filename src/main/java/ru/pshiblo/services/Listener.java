package ru.pshiblo.services;

public interface Listener<T> {
    void handle(T obj);
}
