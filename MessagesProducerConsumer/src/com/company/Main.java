package com.company;

//KEEP CODE THAT IS SYNCHRONIZED TO AN ABSOLUTE MINIMUM

import java.util.Random;

public class Main {
    //Two threads in this application
    //One produces messages
    //One consumes messages
    public static void main(String[] args) {
	    Message message = new Message();
        (new Thread(new Writer(message))).start();
        (new Thread(new Reader(message))).start();


    }
}


//Remember that only one synchronized method can
//run at a time
class Message {
    private String message;
    private boolean empty = true;
    //empty variable will be true when there is no
    //message to read

   //by the consumer
    public synchronized String read() {
        //We want to call wait() within a loop
        //so that when it returns, because there's been
        //a notification of some sort, we'll go back to the
        //beginning of the loop, we check whatever condition
        //we're interested in
        //and then we call wait again if the condition hasn't changed
        //So basically, never assume that a thread has been woken up
        //because the condition that it's waiting on has changed
        while(empty) {
            try {
                wait();
            } catch(InterruptedException e) {

            }
        }
        empty = true;
        notifyAll();
        return message;
        //Now, why are we calling notifyAll instead of notify?
        //Well, because we can't notify a specific thread
        //and that's because the thread doesn't accept any parameters
        //it's conventional to use notifyAll unless we're dealing
        //with a situation when ther are a significant number of threads
        //that all perform a similar task waiting for a lock
        //so in that case we don't want to wak eup every thread
        //because when there's a lot of them, that could result
        //in a huge performance drop.

    }

   //by the producer
    public synchronized void write (String message) {
        while(!empty) {
            try {
                wait();
            } catch(InterruptedException e) {

            }
        }
        empty = false;
        this.message = message;
        notifyAll();
    }

    //now the above two methods, read and write.
    //These have been synchronized because we don't
    //want the threads to read WHILE writing
}

class Writer implements Runnable {
    private Message message;

    public Writer(Message message) {
        this.message = message;
    }

    public void run() {
        String messages[] = {
                "Humpty Dumpty sat on a a wall",
                "Humpty Dumpty had a great fall",
                "All the king's horses and all the king's men",
                "Couldn't put Humpty together again!"
        };
        Random random = new Random();

        for(int i=0; i<messages.length; i++ ) {
            message.write(messages[i]);
            try {
                Thread.sleep(random.nextInt(2000));
            } catch (InterruptedException e) {

            }
        }

        message.write("Finished Writing");
    }

}

class Reader implements Runnable {
    private Message message;

    public Reader(Message message) {
        this.message = message;
    }

    public void run() {
        Random random = new Random();
        for(String latestMessage = message.read(); !latestMessage.equals("Finished Writing"); latestMessage = message.read()) {
            System.out.println(latestMessage);
            try {
                Thread.sleep(random.nextInt(2000));
            } catch(InterruptedException e) {

            }
        }
    }
}


//Some terms:
//Critical Section - Code that needs to be synchronized or are synchronized
//Thread-safe - There won't be thread interference here
//Deadlock - two or more threads blocked forever, waiting
//for each other
//Atomic operation - an operation which is performed as a single
//unit of work without the possibility of interference from
//other operations.
//Java guanrantees that reading or writing a variable is
//an atomic operation UNLESS the variable is of type long
//or double.

//The methods that can only be called within synchronized code
//is weight notify and notify all methods
//the producer and consumer example demonstrates these two methods

//Why are we still looping?
//The answer is that we always want to call wait WITHIN a for loop
//that's testing for whatever condition we're waiting on
//because when a thread is notified to wake up
//there's not guarantee that it's being woken up
//because the condition we're waiting on has changed so it's
//possible the operating system has woken it up for another
//reason.

//Explaining (or trying to) what's going on
//Each thread waits and releases its lock on the message object
//when the loop condition passes and that gives the other thread
//the opportunity to run so it can now process a message
//and change the value of the empty variable. When it calls
//the notify all method, the thread that's waiting can now
//proceed so the two threads go back an forth like this until
//all the messages have been printed.

//Two more points:
//1) When synchornizing code always keep in mind that threads
//can be suspended while executing a single line of code
//so a single line of code may call a method that contains
//many operations that can be suspended at all of those points.
//2) Some collections ARE NOT thread safe
//The ArrayList for example is not thread safe
//Alternatively, we can call collections.synchronize list, the method,
//and pass it to the array list, so we still have to synchronize
//iterating over the list.


//Atomic Operations in Java:
//Reading and writing reference variables
//ex. myObject 1 = myObject 2.
//Thread can't be suspended in the middle of executing that statement

