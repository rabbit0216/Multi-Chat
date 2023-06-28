import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        Socket socket = null;
        BufferedReader in = null;
        Scanner sc = new Scanner(System.in);

        try {
            socket = new Socket("localhost", 8080);
            System.out.println("[Notice] 서버와 연결 되었습니다.");

            System.out.println("[Notice] 이름을 입력해주세요.");
            String name = sc.nextLine();

            System.out.println("[Notice] 접속 대기중..");

            // 사용자로부터 받은 입력을 서버로 전송하는 스레드 실행
            Thread sendThreadToServer = new SendThreadToServer(socket, name);
            sendThreadToServer.start();

            // 서버로부터 전달받은 메시지 출력, 본인이 나갔을 경우 종료
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (in != null) {
                String receivedMsg = in.readLine();
                if (("[Notice] " + name + "님이 나가셨습니다.").equals(receivedMsg)) break;
                System.out.println(receivedMsg);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    class SendThreadToServer extends Thread {
        private Socket socket = null;
        private String name;
        Scanner sc = new Scanner(System.in);

        public SendThreadToServer(Socket socket, String name) {
            this.socket = socket;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                PrintStream out = new PrintStream(socket.getOutputStream()); // 서버 소켓의 outputstream 객체 가져오기

                // 첫 라인은 사용자 이름 전송
                out.println(this.name);
                out.flush();

                // 사용자로부터 입력받은 메시지 전송, exit 입력받으면 종료
                while (true) {
                    String msg = sc.nextLine();
                    out.println(msg);
                    out.flush();

                    if ("exit".equals(msg)) break;
                }

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
