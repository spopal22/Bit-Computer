import java.util.Arrays;

public class ALU {
    public static Word op1;
    public static Word op2;
    public static Word result;

    public ALU() {
        op1 = new Word();
        op2 = new Word();
        result = new Word();
    } //Initialize the operands and the resulting word

    public static void doOperation(Bit[] operation) {
    if (operation.length != 4){
        System.out.println("Operation length is invalid");
        return;
    } //Does the operation using the number of provided bits and checks if the operation array length is valid

    boolean[] testOP = new boolean[4];
    for (int i = 0; i < 4; i++){
        testOP[i] = operation[i].getValue();
    } //Converts the operation bits to a boolean array so that it can be compared

    Word tempResult = new Word();
        //Defines all of the operation codes
        boolean[] andCode = {true, false, false, false};
        boolean[] orCode = {true, true, false, false};
        boolean[] xorCode = {true, false, true, false};
        boolean[] notCode = {true, false, true, true};
        boolean[] leftShiftCode = {true, true, false, false};
        boolean[] rightShiftCode = {true, true, false, true};
        boolean[] addCode = {true, true, true, false};
        boolean[] subtractCode = {false, true, true, false};
        boolean[] multiplyCode = {false, false, true, true};
        //Does the operations based on how it was defined
        if (Arrays.equals(testOP, andCode)) {
            tempResult = op1.and(op2);
        } else if (Arrays.equals(testOP, orCode)) {
            tempResult = op1.or(op2);
        } else if (Arrays.equals(testOP, xorCode)) {
            tempResult = op1.xor(op2);
        } else if (Arrays.equals(testOP, notCode)) {
            tempResult = op1.not();
        } else if (Arrays.equals(testOP, leftShiftCode)) {
            int shiftAmount = (int) op2.getUnsigned() % 32;
            tempResult = op1.leftShift(shiftAmount);
        } else if (Arrays.equals(testOP, rightShiftCode)) {
            int shiftAmount = (int) op2.getUnsigned() % 32;
            tempResult = op1.rightShift(shiftAmount);
        } else if (Arrays.equals(testOP, addCode)) {
            tempResult = add(op1, op2);
        } else if (Arrays.equals(testOP, subtractCode)) {
            tempResult = subtract(op1, op2);
        } else if (Arrays.equals(testOP, multiplyCode)) {
            tempResult = multiply(op1, op2);
        } else {
            System.out.println("Invalid operation.");
            return;
        }
        result.copy(tempResult); //Copies the tempresult
    }

    public static Word add(Word a, Word b) {
        Word sum = new Word();
        Bit carry = new Bit(false); //Initializes it all

        for (int i = 0; i < 32; i++) { //Iterates through all the bits
            Bit bitA = a.getBit(i);
            Bit bitB = b.getBit(i);
            Bit sumBit = bitA.xor(bitB).xor(carry); //Calcs sum from the bits and the carry
            sum.setBit(i, sumBit);
            carry = bitA.and(bitB).or(bitA.and(carry)).or(bitB.and(carry)); //Sets the carry and updates the carry bit
        }

        return sum;
    } //Performs binary adding of two words

    public static Word subtract(Word a, Word b) {
        Word negB = b.not();
        Word result = new Word();
        Bit borrow = new Bit(true); //Initializes and sets to 1 for subtraction

        for (int i = 0; i < 32; i++) {
            Bit bitA = a.getBit(i);
            Bit bitB = negB.getBit(i);
            Bit diffBit = bitA.xor(bitB).xor(borrow);
            result.setBit(i, diffBit);
            borrow = (bitA.and(borrow.not())).or(bitB.and(borrow));
        } //Iterates through the bits, takes the bits, calculates the difference and sets that difference

        return result; //Returns the result
    }

    public static Word multiply(Word a, Word b) {
        Word result = new Word(); //Initializes word object to keep the result

        for (int i = 0; i < 32; i++) { //Iterates through each bit
            if (b.getBit(i).getValue()) {
                Word shiftedA = a.leftShift(i);
                result = add(result, shiftedA); //Checks if bit is1, if its 1 does left shift by i positions. Then adds the shifted bit to the result
            }
        }

        return result; //Returns result
    }

    public Bit add2(Bit bit1, Bit bit2, Bit bitIn) {
        Bit sum = bit1.xor(bit2).xor(bitIn);
        Bit bitOut = (bit1.and(bit2)).or((bit1.xor(bit2)).and(bitIn));
        return sum; //Adds the two bits and returns the result
    }

    public Bit add4(Bit bit1, Bit bit2, Bit bit3, Bit bit4, Bit carry) {
        Bit sum1and2 = bit1.xor(bit2).xor(carry);
        Bit carry1and2 = bit1.and(bit2).or(bit1.and(carry)).or(bit2.and(carry));
        Bit sum3and4 = bit3.xor(bit4).xor(carry1and2);
        Bit carry3and4 = bit3.and(bit4).or(bit3.and(carry1and2)).or(bit4.and(carry1and2));
        Bit sum = sum3and4.xor(sum1and2).xor(carry3and4);
        Bit carryOut = carry3and4.or(sum3and4.and(sum1and2)).or(carry1and2.and(sum1and2));

        return sum; //Does each calculation instead of using add2 and carrys
    }
}
