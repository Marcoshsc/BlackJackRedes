package communication;

import domain.NetworkTransferable;
import server.ConnectionHandler;

import java.io.IOException;
import java.util.List;

public class CommunicationHandler {

    private final ConnectionHandler connectionHandler;

    private CommunicationHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public static CommunicationHandler of(ConnectionHandler connectionHandler) {
        return new CommunicationHandler(connectionHandler);
    }

    public CommunicationAnswer getMessage(List<CommunicationTypes> expectedTypes, List<NetworkTransferable<?>> handlers)
            throws IOException {
        String message = connectionHandler.getDataInputStream().readUTF();
        String[] parts = message.split("#");
        CommunicationTypes receivedType = CommunicationTypes.valueOf(parts[0]);
        int index = expectedTypes.indexOf(receivedType);
        if(index < 0)
            throw new IllegalArgumentException("Received type is different from expected types.");
        if(handlers.get(index) == null)
            return new CommunicationAnswer(receivedType, null);;
        return new CommunicationAnswer(receivedType, handlers.get(index).fromTransferString(parts[1], connectionHandler));
    }

    public <T> void sendMessage(CommunicationTypes type, NetworkTransferable<T> handler, T value) throws IOException {
        String message = String.format("%s#%s", type.toString(), handler.toTransferString(value));
        connectionHandler.getDataOutputStream().writeUTF(message);
    }

    public <T> void sendMessage(CommunicationTypes type, NetworkTransferable<T> handler, String context, T value) throws IOException {
        String message = String.format("%s#%s", type.toString(), handler.toTransferString(value, context));
        connectionHandler.getDataOutputStream().writeUTF(message);
    }

    public void sendMessage(CommunicationTypes type) throws IOException {
        String message = String.format("%s#", type.toString());
        connectionHandler.getDataOutputStream().writeUTF(message);
    }

}
