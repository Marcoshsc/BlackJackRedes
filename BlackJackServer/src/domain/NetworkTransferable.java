package domain;

import server.ConnectionHandler;

public interface NetworkTransferable<T> {

    String toTransferString(T value);
    default String toTransferString(T value, String context) {
        throw new IllegalArgumentException("Not implemented.");
    }
    T fromTransferString(String transferString, ConnectionHandler connectionHandler);

}
