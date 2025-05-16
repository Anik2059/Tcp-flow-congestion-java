/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.tcp.flow.congestion.java;

/**
 *
 * @author admin 
 */
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class TcpFlowCongestionJava {
    private int rwnd; // Receive window in MSS
    private double cwnd; // Congestion window in MSS
    private final int mss; // Max segment size in bytes
    private int ssthresh; // Slow start threshold
    private final int bufferSize; // Receiver buffer size
    private final BlockingQueue<Integer> buffer; // Receiver buffer
    private int lastByteSent;
    private int lastByteAcked;
    private int dupAckCount;
    private String state; // Slow start, congestion avoidance, or fast recovery
    private final ReentrantLock lock;
    private final AtomicBoolean running;
    private final Random random;
    private static final double TIMEOUT = 0.5; // Fake timeout in seconds

    public TcpFlowCongestionJava(int bufferSize, int initialRwnd, double initialCwnd, int mss, int ssthresh) {
        this.bufferSize = bufferSize;
        this.rwnd = initialRwnd;
        this.cwnd = initialCwnd;
        this.mss = mss;
        this.ssthresh = ssthresh;
        this.buffer = new ArrayBlockingQueue<>(bufferSize / mss);
        this.lastByteSent = 0;
        this.lastByteAcked = 0;
        this.dupAckCount = 0;
        this.state = "slow_start";
        this.lock = new ReentrantLock();
        this.running = new AtomicBoolean(true);
        this.random = new Random();
    }

    public void sender() {
        while (running.get()) {
            lock.lock();
            try {
                int effectiveWindow = (int) Math.min(rwnd, cwnd) * mss;
                if (lastByteSent - lastByteAcked <= effectiveWindow) {
                    int bytesToSend = Math.min(effectiveWindow - (lastByteSent - lastByteAcked), mss);
                    if (bytesToSend > 0) {
                        lastByteSent += bytesToSend;
                        System.out.println("Sender: Sent " + bytesToSend + " bytes, cwnd=" + cwnd + ", rwnd=" + rwnd);
                        new Thread(() -> simulateNetwork(bytesToSend)).start();
                    }
                }
            } finally {
                lock.unlock();
            }
            try {
                Thread.sleep(100); // Fake RTT
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void simulateNetwork(int bytesSent) {
        if (random.nextDouble() < 0.05) { // 5% chance of packet loss
            System.out.println("Network: Packet dropped (timeout)");
            handleTimeout();
            return;
        }
        if (random.nextDouble() < 0.05) { // 5% chance of triple dup ACKs
            System.out.println("Network: Triple duplicate ACKs");
            lock.lock();
            try {
                dupAckCount += 3;
                handleAck(true);
            } finally {
                lock.unlock();
            }
            return;
        }
        try {
            Thread.sleep(50); // Fake network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        receiver(bytesSent);
    }

    private void receiver(int bytesReceived) {
        lock.lock();
        try {
            if (buffer.offer(bytesReceived)) {
                rwnd = (bufferSize - buffer.size() * mss) / mss;
                System.out.println("Receiver: Got " + bytesReceived + " bytes, rwnd=" + rwnd);
                handleAck(false);
            } else {
                System.out.println("Receiver: Buffer’s full, rwnd=0");
                rwnd = 0;
            }
        } finally {
            lock.unlock();
        }
    }

    private void handleAck(boolean duplicate) {
        lock.lock();
        try {
            if (duplicate) {
                dupAckCount++;
                if (dupAckCount >= 3 && !state.equals("fast_recovery")) {
                    System.out.println("Sender: Hitting Fast Recovery");
                    ssthresh = Math.max((int) (cwnd / 2), 1);
                    cwnd = ssthresh + 3;
                    state = "fast_recovery";
                }
                return;
            }
            dupAckCount = 0;
            lastByteAcked += mss;
            System.out.println("Sender: Got ACK, lastByteAcked=" + lastByteAcked);

            if (state.equals("slow_start")) {
                cwnd += 1;
                if (cwnd >= ssthresh) {
                    state = "congestion_avoidance";
                    System.out.println("Sender: Switching to Congestion Avoidance");
                }
            } else if (state.equals("congestion_avoidance")) {
                cwnd += 1.0 / cwnd; // Linear bump
            } else if (state.equals("fast_recovery")) {
                cwnd = ssthresh;
                state = "congestion_avoidance";
                System.out.println("Sender: Leaving Fast Recovery for Congestion Avoidance");
            }
        } finally {
            lock.unlock();
        }
    }

    private void handleTimeout() {
        lock.lock();
        try {
            ssthresh = Math.max((int) (cwnd / 2), 1);
            cwnd = 1;
            state = "slow_start";
            dupAckCount = 0;
            System.out.println("Sender: Timeout, back to Slow Start");
        } finally {
            lock.unlock();
        }
    }

    public void run() {
        Thread senderThread = new Thread(this::sender);
        senderThread.start();
        try {
            Thread.sleep(5000); // Run for 5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        running.set(false);
        try {
            senderThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        TcpFlowCongestionJava tcp = new TcpFlowCongestionJava(1000, 500, 1, 1, 16);
        tcp.run();
    }
}