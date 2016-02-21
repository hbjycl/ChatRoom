import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

/**
 * Created by hbjycl on 2016/2/21.
 */
public class Client {
    private Socket s;
    private BufferedReader br;
    private PrintWriter pw;
    private String name;
    private BufferedReader consoleInput;
    private Random random = new Random();
    private boolean flag =true;

    public static void main(String[] args)
    {
        new Client().startup();
    }

    public void startup() {
        try {
            s = new Socket("127.0.0.1", 5858);
            consoleInput = new BufferedReader(new InputStreamReader(System.in));
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pw = new PrintWriter(s.getOutputStream(), true);
            name = "随机的" + random.nextInt(100);
            pw.println(name);
            String inputWord;
            ClientThread ct = new ClientThread();
            new Thread(ct).start();
            while ((inputWord=consoleInput.readLine())!=null)
            {
                pw.println(inputWord);
                if("quit".equalsIgnoreCase(inputWord))
                {
                    flag=false;
                    break;
                }
            }

        } catch (SocketException e)
        {
            System.out.println("主机异常断开，请稍后重连！");
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(consoleInput!=null)
                try {
                    consoleInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void read() throws IOException {
        String readWord;
        while((readWord=br.readLine())!=null)
        {
            System.out.println(readWord);
        }
    }

    private class ClientThread implements Runnable{

        @Override
        public void run() {
            while(true)
            {
                if(!flag)
                {
                    break;
                }
                try {
                    read();
                }
                catch(SocketException e)
                {
                    flag = false;
                    System.out.println("断开了连接");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (s != null)
                        try {
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        }
    }
}
