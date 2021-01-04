import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySocket extends Socket {
    Socket socket;
    BufferedReader bufRe;
    PrintWriter prW;

    public MySocket(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.bufRe = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.prW = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            Logger.getLogger(MySocket.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public MySocket(Socket socket){
        try {
            this.socket = socket;
            this.bufRe = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.prW = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            Logger.getLogger(MySocket.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void println(String s){
        this.prW.println(s);
        this.prW.flush();
    }

    public String readLine(){
        String s = null;
        try {
            s = this.bufRe.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void close(){
        try {
            this.bufRe.close();
            this.prW.close();
            this.socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /// NOU P3 ///
    
    public String read() throws IOException{
        //Llegim el Socket
        String linia = bufRe.readLine();
        return linia;
    }
    
}
