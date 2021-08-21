package domain.enums;

public enum Faces {

    ACE(1, 11),
    TWO(2,2),
    THREE(3,3),
    FOUR(4,4),
    FIVE(5,5),
    SIX(6,6),
    SEVEN(7,7),
    EIGHT(8,8),
    NINE(9,9),
    TEN(10,10),
    JACK(10,10),
    QUEEN(10,10),
    KING(10,10);

    private final int value1;
    private final int value2;

    Faces(int value1, int value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public int getValue1() {
        return value1;
    }

    public int getValue2() {
        return value2;
    }
}
