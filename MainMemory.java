public class MainMemory {
    private static Word[] memory;

    static {
        memory = new Word[1024];
    } //Initialize memory with the appropriate amount of words

    public static Word[] getMemory() {
        return memory;
    }

    public static Word read(Word address) {
        int i = (int) address.getUnsigned();
        if (i >= 0 && i < memory.length) { //Checks that the address is in the valid range, and returns the data or a new word
            return (memory[i] != null) ? memory[i] : new Word();
        } else {
            throw new IllegalArgumentException("Memory address is not valid: " + address);
        }
    }

    public static void write(Word address, Word value) {
        int i = (int) address.getUnsigned();
        if (i >= 0 && i < memory.length) { //Checks if address is in range
            memory[i] = value; //Stores the value in memory at the address
        } else {
            throw new IllegalArgumentException("Memory address is not valid: " + address);
        }
    }

    public static void load(Word[] data) {
        if (data.length > 1024) { //Checks that the data is the right size
            throw new IllegalArgumentException("Data size is greater than memory");
        }
        if (memory == null) {
            memory = new Word[1024]; //If the memory is null it initializes it
        }
        for (int i = 0; i < data.length; i++) { //Iterates through the string in the data array
            if (i >= memory.length) {
                break; //If the current index gets bigger than the memory capacity it breaks
            }
            Word word = new Word(); //Creates a new word to hold the data
            for (int j = 0; j < 32; j++) { //Iterates through the bits
                boolean bitValue = data[i].getBit(j).getValue();
                word.setBit(j, new Bit(bitValue)); //Sets bit to appropriate position in the word
            }
            memory[i] = word;  //Stores word in the memory at the address
        }
    }
}