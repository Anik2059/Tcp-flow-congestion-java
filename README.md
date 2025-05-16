### TCP Flow and Congestion Control Simulator (Java)

Hey there! This is a Java program I put together to simulate TCP’s flow control and congestion control—basically, how TCP makes sure data gets sent reliably without overwhelming the receiver or the network. It’s a simplified version of TCP, using the receive window (rwnd) to manage the receiver’s buffer and the congestion window (cwnd) to handle network traffic. I built this to get a better grip on TCP’s sliding window, slow start, congestion avoidance, and fast recovery (like TCP Reno). If you’re curious about networking or want to see TCP in action, this is a neat little project to check out!

# What Does It Do?

This simulator acts like a TCP connection between a sender and receiver:

Flow Control: The sender uses rwnd to avoid sending more data than the receiver’s buffer can handle. If rwnd hits 0 (buffer’s full), the sender waits until there’s space.

Congestion Control: The sender adjusts cwnd based on network conditions:
Slow Start: cwnd doubles each round-trip time (RTT) to ramp up quickly.
Congestion Avoidance: cwnd grows slowly to avoid clogging the network.
Fast Recovery: If it detects packet loss via triple duplicate ACKs, it cuts cwnd in half and recovers smoothly.


It fakes network issues (5% chance of packet loss or triple duplicate ACKs) to show how TCP reacts to problems.

The sim runs for 5 seconds, printing logs of what’s happening—sending data, receiving, ACKs, and congestion events—so you can see rwnd and cwnd change over time.
What You’ll Need

Java 17 or later (I used Java 17, but newer versions should work too).
A terminal/command line or NetBeans IDE (I used NetBeans 17, but any recent version should do).
No external libraries—just pure Java!

# How to Set It Up

Clone the Repo: Grab the code from GitHub:
git clone https://github.com/your-user-name/tcp-flow-congestion-java.git
cd tcp-flow-congestion-java


# Check the Files: You’ll see:

src/com/mycompany/tcp_flow_congestion/java/TcpFlowCongestionJava.java: The main simulation code.
README.md: This file you’re reading.



# How to Run It

You can run the simulator either from the terminal or in NetBeans. I’ll show you both ways.

Option 1: Run from Terminal

Compile: Navigate to the project folder and compile:
javac src/com/mycompany/tcp_flow_congestion/java/TcpFlowCongestionJava.java


Run: Execute the program:
java -cp src com.mycompany.tcp_flow_congestion.java.TcpFlowCongestionJava



Option 2: Run in NetBeans

Open NetBeans: Fire up NetBeans (I used NetBeans 17, but any recent version should work).

Create a New Project:

Go to File > New Project.
Choose Java > Java Application and click Next.
Name the project (e.g., Tcp-flow-congestion-java).
Uncheck “Create Main Class” (we’ll use TcpFlowCongestionJava.java).
Set the project location to a folder of your choice, then click Finish.


# Add the Code:

Copy the com folder (which contains mycompany/tcp_flow_congestion/java/TcpFlowCongestionJava.java) into the src folder of your NetBeans project. You can drag it into the Source Packages in NetBeans or copy it to the project’s src folder on your computer.
In NetBeans, you should see com.mycompany.tcp_flow_congestion.java with TcpFlowCongestionJava.java inside.


# Run the Program:

Right-click TcpFlowCongestionJava.java in the Project Explorer.
Select Run File (or press Shift+F6).
If NetBeans asks to set it as the main class, click OK.


# Check the Output: The Output window in NetBeans (at the bottom) will show logs.


What’s in the Output?

The logs show the TCP connection in action:
Sender: Sent 1 bytes, cwnd=1.0, rwnd=500
Receiver: Got 1 bytes, rwnd=499
Sender: Got ACK, lastByteAcked=1
Sender: Sent 1 bytes, cwnd=2.0, rwnd=499
Network: Triple duplicate ACKs
Sender: Hitting Fast Recovery


Sender: Sends data, limited by the smaller of rwnd (receiver’s buffer space) and cwnd (network congestion limit).
Receiver: Takes data, updates rwnd as the buffer fills.
Network: Randomly fakes packet loss or triggers duplicate ACKs.
Congestion Control: Shows cwnd growing in slow start, slowing in congestion avoidance, or adjusting in fast recovery.
Flow Control: Ensures the sender respects rwnd.

Each run’s different because of the random network events (5% chance of issues), so you might see more timeouts or smoother runs.
Why This Is Cool
This sim helped me understand how TCP keeps data transfer smooth. Flow control stops the receiver from getting overwhelmed, and congestion control prevents network overload. It’s not a real network (no sockets or packet reordering), but it shows the big ideas—like how TCP Reno’s fast recovery is better than older versions (like Tahoe) and how it avoids silly window syndrome by not sending tiny packets.
Troubleshooting

Terminal Errors? Check your Java version (java -version) and make sure you’re in the right directory. Double-check the package path in the java command.
NetBeans Not Running? Ensure TcpFlowCongestionJava.java is in the src/com/mycompany/tcp_flow_congestion/java folder in your project. Set it as the main class if needed (right-click > Properties > Run > set Main Class to com.mycompany.tcp_flow_congestion.java.TcpFlowCongestionJava).
No Output? Confirm you compiled and ran correctly. In NetBeans, check the Output window at the bottom.
Weird Logs? Random packet loss can make runs vary. Try running it a few times to see different scenarios.

Want to Tweak It?
Feel free to play with the code:

Change bufferSize (1000) or initialRwnd (500) to test a smaller buffer.
Adjust ssthresh (16) to switch from slow start to congestion avoidance earlier.
Lower TIMEOUT (0.5s) or sleep times (100ms RTT, 50ms delay) for a faster sim.

Edit TcpFlowCongestionJava.java, then recompile (terminal) or rerun (NetBeans).

# Contributors

Anik (that’s me, the one who coded this up!)

# License

MIT License—use it, share it, or mess with it however you like.

Questions?

If something’s not working or you want to chat about TCP, open an issue on the GitHub repo or hit me up (you can add your contact here if you want). Have fun with the sim!
