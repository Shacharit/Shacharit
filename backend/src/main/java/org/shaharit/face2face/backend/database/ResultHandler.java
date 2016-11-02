package org.shaharit.face2face.backend.database;

interface ResultHandler<T> {
    void processResult(T result);
}
