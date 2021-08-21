package domain.enums;

public enum Shape {

    CIRCLE, X;

    public static String toPresentation(Shape shape) {
        if(shape == null)
            return " ";
        return shape == CIRCLE ? "O" : "X";
    }

    public static Shape valueOfNullable(String value) {
        if(value.equals("null"))
            return null;
        return Shape.valueOf(value);
    }

}
