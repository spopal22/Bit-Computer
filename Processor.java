class Processor {
    private Word PC;
    private Word SP;
    private Bit halted; //Holds the PC, SP, and a flag for processor if it gets halted
    private Word[] registers;
    private Word[] result;

    public Processor() {
        PC = new Word();
        SP = new Word();
        PC.set(0);
        SP.set(0);
        halted = new Bit(false); //Initialize the PC and SP and set initial values
        registers = new Word[32];
        result = new Word[32];
        //Initialize flag to false
    }

    public void run() {
        result = new Word[registers.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Word(); // Initialize each element of the result array
        }

        while (!halted.getValue()) { // Loops until flag is true
            Word instruction = fetch();
            decode(instruction);
            execute(instruction);
            store(instruction, result); // Fetches the instruction, decodes, executes, and stores the result
        }
    }

    private Word fetch() {
        Word instruction = MainMemory.read(PC); //Read the instruction from the PC
        PC.increment(); //Incrememnt the PC
        return instruction; //Return the instruction that we fetch
    }
    private void decode(Word instruction){
        boolean bit30 = instruction.getBit(30).getValue();
        boolean bit31 = instruction.getBit(31).getValue(); // Extract the bits at that position for both

        int format = (bit31 ? 2 : 0) + (bit30 ? 1 : 0);

        Word rd = new Word();
        Word rs = new Word();
        Word rs1 = new Word();
        Word rs2 = new Word();

        switch(format) { // Runs different cases based off of instruction and the bits
            case 0:
                break;
            case 1:
                rd = extractBits(instruction, 5, 9); // Extracts rd
                if (rd.getBit(0).getValue() || rd.getBit(1).getValue() || rd.getBit(2).getValue() || rd.getBit(3).getValue() || rd.getBit(4).getValue()) {
                    rd.set(0);
                } //Sets R0 value to 0
                rs = new Word(); // Create Word for source register
                break;
            case 2:
                rs = extractBits(instruction, 5, 9); // Extracts the source
                rd = extractBits(instruction, 10, 14); // Extracts the destination
                if (rd.getBit(0).getValue() || rd.getBit(1).getValue() || rd.getBit(2).getValue() || rd.getBit(3).getValue() || rd.getBit(4).getValue()) {
                    rd.set(0);
                } //Sets R0 value to 0
                break;
            case 3:
                rs1 = extractBits(instruction, 5, 9); // Extracts source 1
                rs2 = extractBits(instruction, 10, 14); // Extracts source 2
                rd = extractBits(instruction, 15, 19); // Extracts destination
                if (rd.getBit(0).getValue() || rd.getBit(1).getValue() || rd.getBit(2).getValue() || rd.getBit(3).getValue() || rd.getBit(4).getValue()) {
                    rd.set(0);
                } //Sets R0 value to 0
                break;
        }
    }

    private Word extractBits(Word instruction, int startBit, int endBit) { //Extract bits from instruction
        Word registers = new Word();
        int numBits = endBit - startBit + 1;
        for (int i = 0; i < numBits; i++) {
            boolean bitValue = instruction.getBit(startBit + i).getValue(); //Gets the bit at the specific position and stores it
            registers.setBit(i, new Bit(bitValue));
        }
        return registers;
    }


    private void execute(Word instruction) {
        Word opcodeBits = extractBits(instruction, 27, 31); //Extracts opcode from instruction

        if (isMathOrHaltOpcode(opcodeBits)) { //Checks if opcode is a mathop or halt
            if (isMathOpcode(opcodeBits)) { //If its mathop it continues
                Word OP1 = extractBits(instruction, 15, 19);
                Word OP2 = extractBits(instruction, 20, 24); //Extracts the appropriate bits

                ALU.op1 = OP1;
                ALU.op2 = OP2;

                ALU.doOperation(extractFunctionBits(instruction)); //Does the operation with the ALU
                result[0] = ALU.result; //Stores the result in an array
            } else {
                halted.set(true); //Sets flag for halted to true
            }
        }
    }

    private boolean isMathOrHaltOpcode(Word opcodeBits) {
        for (int i = 27; i <= 30; i++) {
            if (opcodeBits.getBit(i).getValue()) {
                return false;
            }
        }
        return true; //Checks if its mathop or halt
    }

    private boolean isMathOpcode(Word opcodeBits) {
        if (!opcodeBits.getBit(31).getValue()) {
            return false;
        }
        for (int i = 27; i <= 30; i++) {
            if (opcodeBits.getBit(i).getValue()) {
                return false;
            }
        }
        return true; //Checks if its mathop
    }
    private Bit[] extractFunctionBits(Word instruction) {
        Bit[] functionBits = new Bit[4]; //Extract bits from instruction

        for (int i = 0; i < 4; i++) {
            boolean bitValue = instruction.getBit(i).getValue();
            functionBits[i] = new Bit(bitValue); //Gets value at the position and stores it in an array
        }

        return functionBits;
    }

    private void store(Word instruction, Word[] result) {
        int wordSize = 32;

        Word[] memory = MainMemory.getMemory();  //Result gets stored in memory

        Word rd = extractBits(instruction, 5, 9); //Extracts the appropriate bits

        int index = (int) rd.getUnsigned();
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < wordSize; j++) {
                boolean bitValue = result[i].getBit(j).getValue();
                memory[index + i].setBit(j, new Bit(bitValue)); //Every word of result gets stored in the memory
            }
        }
    }
    private void branch(Word instruction) {
        Word opcodeBits = extractBits(instruction, 28, 31); //Extracts opcode bits so we know the instruction

        if (opcodeBits.equals(new Word().update(1))) { //Checks to see if it corresponds to a branch operation or not
            Word rs1 = extractBits(instruction, 5, 9);
            Word rs2 = extractBits(instruction, 10, 14);
            Word imm = extractBits(instruction, 15, 19); //Extracts all needed bits

            Word eq = new Word();
            Word ne = new Word();
            Word lt = new Word();
            Word ge = new Word();
            Word gt = new Word();
            Word le = new Word(); //Different flags for different conditions

            boolean condition = false;
            switch (instruction.getBit(24).getValue() ? 4 : 0 | instruction.getBit(25).getValue() ? 2 : 0 | instruction.getBit(26).getValue() ? 1 : 0) {
                case eq:
                    condition = rs1.equals(rs2);
                    break;
                case ne:
                    condition = !rs1.equals(rs2);
                    break;
                case lt:
                    condition = rs1.getSigned() < rs2.getSigned();
                    break;
                case ge:
                    condition = rs1.getSigned() >= rs2.getSigned();
                    break;
                case gt:
                    condition = rs1.getSigned() > rs2.getSigned();
                    break;
                case le:
                    condition = rs1.getSigned() <= rs2.getSigned();
                    break; //Does the condition based off of the opcode
            }

            int pcNew = (int) (PC.getUnsigned() + imm.getSigned()); //Calcs new PC value

            if (condition) {
                PC.set(pcNew); //Updates PC if conditon is true
            }

            if (opcodeBits.equals(new Word().update(20))) {
                SP.decrement();
                MainMemory.write(SP, PC); //Handles call and decremtens SP and stores address
            }
        }
    }

    private void call(Word instruction) {
        Word opcodeBits = extractBits(instruction, 28, 31); //Extracts opcode to identify call

        if (opcodeBits.equals(new Word().update(20))) { //Checks if its a call operation
            Word rd = extractBits(instruction, 5, 14); //Extracts register

            int newPC;
            if (instruction.getBit(25).getValue()) {
                newPC = (int) rd.getUnsigned();
            } else {
                newPC = (int) (PC.getUnsigned() + rd.getSigned());
            } //Calcs the new PC based off instruction

            SP.decrement();
            MainMemory.write(SP, PC); //Decrements SP and keeps return address

            PC.set(newPC); //Update PC to new value
        }
    }

    private void push(Word instruction) {
        Word opcodeBits = extractBits(instruction, 28, 31); //Extracts bits to identify push

        if (opcodeBits.equals(new Word().update(30))) { //Checks if its a push operation
            Word rs = extractBits(instruction, 5, 14); //Extractts register

            int value;
            if (instruction.getBit(25).getValue()) {
                value = (int) rs.getUnsigned();
            } else {
                value = (int) (PC.getUnsigned() + rs.getSigned());
            } //Finds value to push basied on instruction

            MainMemory.write(SP, new Word().update(value));
            SP.decrement(); //Writes value and decrements SP
        }
    }

    private void pop(Word instruction) {
        Word opcodeBits = extractBits(instruction, 28, 31); //Extracts bits for pop

        if (opcodeBits.equals(new Word().update(31))) { //Chceks if its a pop operation
            int addressToPop = (int) SP.getUnsigned(); //Gets address from where we are to pop

            Word popped = MainMemory.read(new Word().update(addressToPop)); //Reads value at the address
            SP.increment(); //Increments SP for pop
        }
    }

    private void returnpop() {
        int popFrom = (int) SP.getUnsigned(); //Gets address to pop the return address from

        Word returnAdd = MainMemory.read(new Word().update(popFrom)); //Reads return address

        SP.increment(); //Increments sp accordingly for pop
        PC.copy(returnAdd); //Sets pc to return address
    }

    private void load(Word instruction) {
        Word start = extractBits(instruction, 5, 9);
        Word offset = extractBits(instruction, 10, 19);
        Word end = extractBits(instruction, 20, 24); //Extracts all from the instruction so we know load parameters

        ALU.op1 = start;
        ALU.op2 = offset;
        ALU.doOperation(new Bit[]{new Bit(false), new Bit(true), new Bit(false), new Bit(false)}); //Calcs using address and adding the start and offset

        Word loadedValue = MainMemory.read(ALU.result); //Reads value from memory at what we just calculated

        registers[(int) end.getUnsigned()].copy(loadedValue); //stores that value in register
    }

    private void store(Word instruction) {
        Word start = extractBits(instruction, 5, 9);
        Word offset = extractBits(instruction, 10, 19);
        Word source = extractBits(instruction, 20, 24); //Extracts all from the instruction s we know store parameters

        ALU.op1 = start;
        ALU.op2 = offset;
        ALU.doOperation(new Bit[]{new Bit(false), new Bit(true), new Bit(false), new Bit(false)}); //Calcs using address and adding the start and offset

        MainMemory.write(ALU.result, registers[(int) source.getUnsigned()]); //Writes value into memory at where we calculated
    }
}
