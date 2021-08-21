package domain;

public interface NetworkTransferable<T> {

    String toTransferString(T value);
    T fromTransferString(String transferString);

}
