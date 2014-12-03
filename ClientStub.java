import java.io.*;
import java.net.*;

class ClientStub
{
BufferedReader in;
PrintStream out;
boolean exit;

ClientStub() {
        in   = new BufferedReader(new InputStreamReader(System.in));
        out  = new PrintStream(System.out);
        exit = false;
}

boolean sayHello(BufferedReader srvIn, PrintWriter srvOut)
throws IOException {
        out.println("Enter desired Name");
        String name = in.readLine();
        srvOut.println("HELLO-FROM "+name); // ( supposed to print to server)

        String reply = srvIn.readLine();
        out.println(reply);
        if(reply.startsWith("HELLO")) {
                return true;
        } else {
                return false;
        }
}

void handleInput(BufferedReader srvIn, PrintWriter srvOut)
throws IOException {
        String line = in.readLine();
        if(line == null) {
                out.println("could not read line from user");
                return;
        }

        /* supported commands:
           !who          - perform a WHO request to the server
           @<user> <msg> - send <msg> to <user>
           !exit         - stop the program */
        if(line.equals("!who")) {
                requestUserList(srvIn, srvOut);
        }
        else if(line.startsWith("@")) {
                sendMessage(line, srvIn, srvOut);
        }
        else if(line.equals("!exit")) {
                exit = true;
        }
        else {
                out.println("unknown command");
        }
}

void requestUserList(BufferedReader srvIn, PrintWriter srvOut)
throws IOException {
        srvOut.println("WHO");
        out.println(srvIn.readLine());
}

void sendMessage(String line, BufferedReader srvIn, PrintWriter srvOut)
throws IOException {

        String message = line.split("@")[1];
        srvOut.println("SEND "+message);

        String reply = srvIn.readLine();

        if (reply.startsWith("BAD-RQST-HDR")) {
                out.println("error : bad header");
        } else if (reply.startsWith("UNKNOWN")) {
                out.println("error : unknown user");
        } else if (reply.startsWith("BAD-RQST-BODY")) {
                out.println("error : message notation");
        } else {
                out.println("message succesfully sent");
        }
}

void recvMessage(BufferedReader srvIn)
throws IOException {
        out.println(srvIn.readLine());
}

void start(String[] argv)
throws IOException, UnknownHostException {
        if(argv.length != 2) {
                out.println("usage: java ClientStub <server> <port>");
                return;
        }
        //argv[0] = "wolkje-69.cs.vu.nl";
        //argv[1] = "5378";
        Socket socket        = new Socket(argv[0], Integer.parseInt(argv[1]));
        BufferedReader srvIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter srvOut   = new PrintWriter(socket.getOutputStream(),true);

        if(!sayHello(srvIn, srvOut)) {
                /* todo: cleanup and exit */
                exit = true;
                return;
        }

        while(!exit) {
                try {
                        if(in.ready()) {
                                handleInput(srvIn, srvOut);
                        }
                        if(srvIn.ready()) {
                                recvMessage(srvIn);
                        }
                        Thread.sleep(200);
                }
                catch(IOException e) {
                        out.println(e.getMessage());
                }
                catch(InterruptedException e) {}
        }
        out.println("Closing connection....");
        socket.close();
        /* todo: close the Socket */
}

public static void main(String[] argv)
throws IOException {
        new ClientStub().start(argv);
}
}
