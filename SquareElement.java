public class SquareElement {
    public final int value;
    public final boolean mutable; // items are immutable if they were part of the partial square

    public SquareElement(int value) {
        this.value = value;
        mutable = true;
    }

    public SquareElement(int value, boolean mutable) {
        this.value = value;
        this.mutable = mutable;
    }

    public SquareElement(Candidate c) {
        value = c.treatment;
        mutable = true;
    }

    public boolean empty() { return value == 0; }
    public boolean is(int other) { return value == other; }
    
    public String getStrVal() {
        return (value > 0) ? value + "" : ".";
    }
}