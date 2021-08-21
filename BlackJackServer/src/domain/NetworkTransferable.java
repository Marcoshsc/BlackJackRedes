package domain;

import server.ConnectionHandler;

public interface NetworkTransferable<T> {

    String toTransferString(T value);
    T fromTransferString(String transferString, ConnectionHandler connectionHandler);

}
