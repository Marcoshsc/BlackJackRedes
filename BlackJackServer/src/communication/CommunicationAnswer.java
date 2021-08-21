package communication;

import communication.CommunicationTypes;

public class CommunicationAnswer {

    private final CommunicationTypes type;
    private final Object value;

    public CommunicationAnswer(CommunicationTypes type, Object value) {
        this.type = type;
        this.value = value;
    }

    public CommunicationTypes getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
