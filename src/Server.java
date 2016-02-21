import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbjycl on 2016/2/21.
 */
public class Server {
    private List<ServerThread> sts;
    private ServerSocket ss;

    public static void main(String[] args) {
        new Server().startup();
    }

    public void startup() {
        try {
            ss = new ServerSocket(5858);
            sts = new ArrayList<>();
            Socket s;
            while (true) {
                s = ss.accept();
                ServerThread st = new ServerThread(s);
                new Thread(st).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ss != null)
                    ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class ServerThread implements Runnable {
        private BufferedReader br = null;
        private PrintWriter out = null;
        private String name;
        private Socket s;
        private boolean flag = true;

        public ServerThread(Socket s) throws IOException {
            this.s = s;
            sts.add(this);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            name = br.readLine() + "(" + s.getInetAddress().getHostName() + s.getPort() + ")";
            out = new PrintWriter(s.getOutputStream(), true);
            sendToEveryOne("欢迎"+name + "加入本聊天室！");
        }

        public void readIn() throws IOException {
            String inputWord;
            System.out.println(name + "加入了房间");
            while ((inputWord = br.readLine()) != null) {
                if ("quit".equalsIgnoreCase(inputWord)) {
                    leave();
                    break;
                }
                System.out.println(name + ":" + inputWord);
                sendToEveryOne(name+":"+inputWord);
            }

        }

        private void sendToEveryOne(String inputWord) {
            for (ServerThread st : sts) {
                try {
                    out = new PrintWriter(st.s.getOutputStream(), true);
                    out.println(inputWord);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void leave() {
            flag = false;
            sendToEveryOne(name + "离开了本房间");
            sts.remove(this);
            System.out.println(name+"离开了房间");
        }

        @Override
        public void run() {
            while (true) {
                if (!flag) {
                    break;
                }
                try {
                    readIn();
                }
                catch (SocketException e) {
                    leave();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (s != null) try {
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
