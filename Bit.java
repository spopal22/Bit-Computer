public class Bit {
    boolean bit;

    public Bit(boolean initialValue) {
        bit = initialValue;
    } //Initial value for the bitch as t/f

    public void set(Boolean value) {
        bit = value;
    } //Allows you to change the value to t/f

    public void toggle() {
        bit = !bit;
    } //Toggles the bit changing it from t to f and vice versa

    public void set() {
        bit = true;
    } //Sets bit to true

    public void clear() {
        bit = false;
    } //Sets bit to false

    public boolean getValue() {
        return bit;
    } //Gets current boolean value of the bit, so it can be retrieved by code

    public Bit and(Bit other) {
        if (bit) {
            if (other.getValue()) {
                return new Bit(true);
            } else {
                return new Bit(false);
            }
        } else {
            return new Bit(false);
        }
    } //Does the AND operation with a different bit and gives a bit that is the result of the operation

    public Bit or(Bit other) {
        if (bit) {
            return new Bit(true);
        } else {
            if (other.getValue()) {
                return new Bit(true);
            } else {
                return new Bit(false);
            }
        }
    } //Does the OR operation with a different bit and gives a bit that is the result of the operation

    public Bit xor(Bit other) {
        if (bit) {
            if (other.getValue()) {
                return new Bit(false);
            } else {
                return new Bit(true);
            }
        } else {
            return new Bit(other.getValue());
        }
    } //Does the XOR operation with a different bit and gives a bit that is the result of the operation

    public Bit not() {
        Bit result = new Bit(!this.bit);
        return result;
    } //Does the NOT operation and gives a new bit as a result

    public String toString() {
        if (this.bit) {
            return "t";
        } else {
            return "f";
        }
    }
} //Gives a string of the bit with t for true and f for false being separated by commas. Makes it a lot easier to read