package servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    
    static final int PORT = 1234;
    
    public static void main(String[] args) throws IOException, InterruptedException{
        
        System.out.println("A iniciar o servidor......");
        
        PedidosCURL pedidos = new PedidosCURL();
        String admintoken = pedidos.getToken(pedidos.insereUserNaOrg("admin","org1"));
        pedidos.criarCanal(admintoken,"mychannel");
        String[] peers = {"peer1","peer2"}; 
        Thread.sleep(5000);
        pedidos.joinCanal(admintoken,"mychannel",peers);
        
        List<String> users = new ArrayList<String>();
        List<String> userspasswords = new ArrayList<String>();
        List<String> userstoken = new ArrayList<String>();
        List<String> balancos = new ArrayList<String>();
        Boolean free = true;
        
        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Servidor à espera de conexões na porta "+PORT+"......\n\n");
        
        while(true){
            
            Socket clientsocket = serverSocket.accept();
            
            (new ServidorThread(clientsocket,users,userspasswords,userstoken, balancos,free)).start();
            
        }
        
    
    }

}
