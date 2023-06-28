/**
 * [Server]
 * 클라이언트가 접속 했는지 안했는지 확인 용도
 * 클라이언트가 접속 시 Socket.accept()를 하여 요청을 받는다.
 * 스레드풀을 이용하여 접속 가능한 인원을 제한시킨다.
 *
 * !! BufferedWriter 가 아닌 PrintWriter 사용 이유 !!
 *  PrintWriter 의 경우 print(), println(), printf() 와 같은 다양한 출력함수 제공 => 파일 출력 간편함
 *
 * !! 동기화에서의 Vector vs ArrayList !! L.63
 *  ArrayList 사용하는 것이 성능적으로 더 좋음 (Vector는 Obsolete, Deprecated)
 *  => Collections.synchronizedList()를 사용하자
 * 참고 : https://inpa.tistory.com/entry/JCF-%F0%9F%A7%B1-ArrayList-vs-Vector-%EB%8F%99%EA%B8%B0%ED%99%94-%EC%B0%A8%EC%9D%B4-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0
 *
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    private static final int PORT_NUM = 8080;
    private static final int THREAD_CNT = 2;


    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_CNT); // 스레드 풀, 최대 개수 제한

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
        try {
            serverSocket = new ServerSocket(PORT_NUM);
            while (true) {
                System.out.println("[사용자 접속 대기중...]");
                socket = serverSocket.accept();

                // client가 접속하면 새로운 스레드 생성
                threadPoolExecutor.execute(new ReceievedThreadByClient(socket));
                // System.out.println(threadPoolExecutor);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    executorService.shutdown();
                    System.out.println("[서버 종료]");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("[서버 소켓 통신 에러]");
                }
            }
        }
    }
}

class ReceievedThreadByClient extends Thread {
    static List<PrintWriter> clientWriters = Collections.synchronizedList(new ArrayList<>()); // 접속한 클라이언트 writer 객체 리스트
    Socket socket = null;
    BufferedReader fromClient = null;
    PrintWriter currentClientWriter = null;

    public ReceievedThreadByClient(Socket socket) {
        this.socket = socket;

        // client 소켓 정보로 초기화
        try {
            fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            currentClientWriter = new PrintWriter(socket.getOutputStream());
            clientWriters.add(currentClientWriter);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
//        System.out.println(clientWriters);
    }

    @Override
    public void run() {
        String name = "";
        try {
            // 첫 라인은 사용자 이름을 받음
            name = fromClient.readLine();
            System.out.println("[" + name + " 연결 생성]");
            sendToAll("[Notice] " + name + "님이 접속하셨습니다.");

            while (fromClient != null) {
                String clientMsg = fromClient.readLine();
                if ("exit".equals(clientMsg)) break;
                sendToAll(name + " : " + clientMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[" + name + " 접속 끊김]");
            throw new RuntimeException(e);
        } finally {
            sendToAll("[Notice] " + name + "님이 나가셨습니다.");
            clientWriters.remove(currentClientWriter);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        System.out.println("[" + name + " 연결 종료]");
    }

    private void sendToAll(String msg) {
        int num = 0;
        final int THREAD_CNT = 2;
        for (PrintWriter out : clientWriters) {
            out.println(msg);
            out.flush();
            num++;
            if(num == THREAD_CNT) break;
        }
    }
}
