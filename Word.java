import java.util.StringJoiner;

public class Word {
    private Bit[] bits;

    public Word() {
        bits = new Bit[32];
        for (int i = 0; i < 32; i++) {
            bits[i] = new Bit(false);
        }
    } //Initializes Word with 32 bits and sets them all to false

    public Word update(int value) {
        for (int i = 0; i < 32; i++) {
            boolean bitValue = (value & (1 << (31 - i))) != 0;
            bits[i].set(bitValue); // Set the bit at index i with the corresponding bit value
        }
        return null;
    }

    public void clear() {
        for (int i = 0; i < 32; i++) {
            bits[i].clear(); // Clear each bit in the Word
        }
    }

    public Bit getBit(int i) {
        if (i >= 0 && i < 32) {
            return new Bit(bits[i].getValue());
        } else {
            System.out.println("i needs to be greater than or equal to 0 and less than 32");
        }
        return null;
    } // Outputs a copy of the bits position as a new bit object

    public void setBit(int i, Bit value) {
        if (i >= 0 && i < 32) {
            bits[i].set(value.getValue());
        } else {
            System.out.println("i needs to be greater than or equal to 0 and less than 32");
        }
    } //Takes bit at the specified position and sets the value of it t/f

    public Word and(Word other) {
        Word result = new Word();

        for (int i = 0; i < 32; i++) { //Does the AND operation for each bit
            boolean resultBitValue = this.bits[i].getValue() && other.getBit(i).getValue();
            result.setBit(i, new Bit(resultBitValue));
        }
        return result; //Gives result of the operation
    }

    public Word or(Word other) {
        Word result = new Word();

        for (int i = 0; i < 32; i++) { //Does the OR operation on each bit
            boolean resultBitValue = this.bits[i].getValue() || other.getBit(i).getValue();
            result.setBit(i, new Bit(resultBitValue));
        }
        return result; //Gives the result of the operation
    }

    public Word xor(Word other) {
        Word result = new Word();

        for (int i = 0; i < 32; i++) { //Does the XOR operation for each bit
            boolean resultBitValue = this.bits[i].getValue() ^ other.getBit(i).getValue();
            result.setBit(i, new Bit(resultBitValue));
        }
        return result; //Gives the result of the operation
    }

    public Word not() {
        Word result = new Word();

        for (int i = 0; i < 32; i++) { //Does the NOT operation for each bit
            boolean resultBitValue = !this.bits[i].getValue();
            result.setBit(i, new Bit(resultBitValue));
        }
        return result; //Gives the result of the operation
    }

    public Word rightShift(int amount) {
        Word result = new Word();

        for (int i = 0; i < 32; i++) { //Shifts each bit to the right by specified amount
            int newIndex = i + amount;
            boolean resultBitValue = (newIndex < 32) && this.bits[newIndex].getValue();
            result.setBit(i, new Bit(resultBitValue));
        }

        return result; //Gives the result after the shift
    }

    public Word leftShift(int amount) {
        Word result = new Word();

        for (int i = 0; i < 32; i++) { //Shifts each bit to the left by the specified amount
            int newIndex = i - amount;
            boolean resultBitValue = (newIndex >= 0) && (newIndex < 32) && this.bits[newIndex].getValue();
            result.setBit(i, new Bit(resultBitValue));
        }

        return result; //Gives the result after the shift
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(",");

        for (int i = 0; i < 32; i++) { //Takes each bit and makes a string of either t or f based off the bit value
            joiner.add(bits[i].getValue() ? "t" : "f");
        }

        return joiner.toString(); //Outputs the string result
    }

    public long getUnsigned() {
        long unsignedValue = 0;

        for (int i = 0; i < 32; i++) {
            if (bits[i].getValue()) {
                unsignedValue |= 1L << (31 - i); //Calculates the unsigned value using OR
            }
        }
        return unsignedValue; //Gives output of the calculation
    }

    public int getSigned() {
        int signedValue = 0;

        if (bits[31].getValue()) { //Looks at the most significant bit
            for (int i = 0; i < 31; i++) {
                signedValue |= 1 << (31 - i);
            }
            signedValue = -signedValue;
        } else {
            signedValue = (int) getUnsigned(); //If its positive, casts the result to int
        }

        return signedValue;
    }

    public void copy(Word other) {
        for (int i = 0; i < 32; i++) {
            this.bits[i].set(other.bits[i].getValue());
        }
    } //Copies the value of the bits from another Word to this Word

    public void set(int value) {
        for (int i = 0; i < 32; i++) {
            boolean bitValue = (value & (1 << i)) != 0;
            this.bits[i].set(bitValue);
        }
    } //Takes each bit of the int value and sets it to same bit in this Word

    public void increment() {
        boolean carry = true; //Set carry to true

        for (int i = 0; i < 32 && carry; i++) { //Iterates through each bit
            boolean currentBit = bits[i].getValue(); //Gets the current bit value
            boolean newBit = currentBit ^ true; //Inverts the bit
            bits[i].set(newBit); //Sets the new bit as the inverted one

            carry = currentBit && carry; //Updates the carry
        }
    }

    public void decrement() {
        boolean carry = true;

        for (int i = 0; i < 32 && carry; i++) {
            boolean currentBit = !bits[i].getValue();
            bits[i].set(currentBit);

            carry = !currentBit && carry;
        }
    }
}