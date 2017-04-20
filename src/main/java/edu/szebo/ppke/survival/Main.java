package edu.szebo.ppke.survival;

import java.io.IOException;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.szebo.ppke.survival.brain.Brain;
import edu.szebo.ppke.survival.proto.*;
import edu.szebo.ppke.survival.proto.Communication.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static Logger log = LoggerFactory.getLogger(Main.class.getName());

    private static int getLength() {
        boolean reachedLineEnd = false;
        int result = 0;
        String charsRed = "";
        while (!reachedLineEnd) {
            try {
                byte b = (byte) System.in.read();
                if (b == 0x0A) {
                    reachedLineEnd=true;
                    result = Integer.parseInt(charsRed);
                } else {
                    charsRed = charsRed + (char) b;
                    log.debug("Characters red so far: " + charsRed);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException nf) {
                log.error("Couldn't convert input length to a number: " + charsRed + "; error: " + nf.getMessage());
                return -1;
            }
        }
        return result;
    }

    private static Message readMessage(int inputLength) {
        byte[] buff = new byte[inputLength];
        try {
            int lengthRead = System.in.read(buff, 0, inputLength);
            // read line end:
            //noinspection ResultOfMethodCallIgnored
            System.in.read();

            if (lengthRead == -1) {
                log.error("Standard In stream ended!! Can't expect more: exiting.");
                System.exit(2);
            } else if (lengthRead != inputLength) {
                log.error("Couldn't read in the promised length: " + inputLength + "; got " + lengthRead + " instead! Dropping message.");
                log.debug("Buffer had " + buff.length + " bytes.");
            } else {
                log.info("Could read in message in expected length of " + inputLength + "!");
                try {
                    Message message = Message.PARSER.parseFrom(new String(buff).getBytes());
                    log.info("Received message: \n" + Utils.messageToNiceString(message));
                    return message;
                } catch (InvalidProtocolBufferException e) {
                    log.error("Input message format was wrong: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to read input: " + e.getMessage(), e);
        }
        log.error("Failed to read and/or parse an input message!");
        return null;
    }

    public static void writeAnswer(Communication.Answer answer) {
        log.info("Our answer what we send is: \n" + answer.toString());
        try {
            System.out.write(answer.toByteArray());
            System.out.println();
        } catch (IOException e) {
            log.error("Failed to write response of " + answer.toString() +". Reason: " + e.getMessage(), e);
        }
    }

	public static void main(String[] args) {

        log.info("We started up, it's a happy day :-) ");

        Brain mind = new SimpleMind1();

        log.info("We'll use " + mind.getClass().getSimpleName() + " to think");

        //noinspection InfiniteLoopStatement
        while (true) {

            int inputLength = getLength();
            if (inputLength < 0) {
                log.error("Wrong input length of " + inputLength + "; skip reading and processing.");
            } else {
                Message msg = readMessage(inputLength);
                if (null != msg) {
                	log.info("Recieved non null message, we can decide!");
                    Communication.Answer answer = mind.decideBasedOn(msg);
                    writeAnswer(answer);
                }
            }
        }
    }
}
