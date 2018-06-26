package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidor.PedidosCURL;
import servidor.ServidorThread;

public class Cliente {
    
    static final int PORT = 1234;
    protected Socket serversocket = null;
    
    public Cliente() throws IOException{
        InetAddress address = InetAddress.getByName("localhost");
        this.serversocket = new Socket(address, PORT);
    }
    
    public String comandoString(String message){
        
        String res = "";
        Boolean found = false;
        
        for(int i=0; ((i<message.length())&&(!found)); i++){
            if(message.charAt(i)=='_'){
                found=true;
            }
            else{
                res+=message.charAt(i);
            }
        }
        
        return(res);
        
    }
    
    //String: registar_nome_password_balanco
    //Devolve token ou INSUCESSO
    public String registarUtilizador(String nome, String password, String balanco) throws IOException{
        
        String mensagem = "registar_"+nome+"_"+password+"_"+balanco;
        
        OutputStream os = this.serversocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(mensagem+"\n");
        bw.flush();
        //System.out.println("Messagem enviada: "+mensagem);
        
        //Receber mensagem do servidor
        InputStream is = this.serversocket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        
        String messageserver = br.readLine();
        //System.out.println("Messagem recebida do servidor: " +messageserver);
        
        return(messageserver);
        
    }
    
    //String: login_nome_password_token
    //Resposta: SUCESSO ou INSUCESSO
    public String loginUtilizador(String nome, String password, String token) throws IOException{
        
        String mensagem = "login_"+nome+"_"+password+"_"+token;
        
        OutputStream os = this.serversocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(mensagem+"\n");
        bw.flush();
        //System.out.println("Messagem enviada: "+mensagem);
        
        //Receber mensagem do servidor
        InputStream is = this.serversocket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        
        String messageserver = br.readLine();
        //System.out.println("Messagem recebida do servidor: " +messageserver);
        
        return(messageserver);
        
    }
    
    //String: balanco_user
    //Resposta: balanco
    public String getBalanco(String user) throws IOException{
        
        String mensagem = "balanco_"+user;
        
        OutputStream os = this.serversocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(mensagem+"\n");
        bw.flush();
        //System.out.println("Messagem enviada: "+mensagem);
        
        //Receber mensagem do servidor
        InputStream is = this.serversocket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        
        String messageserver = br.readLine();
        //System.out.println("Messagem recebida do servidor: " +messageserver);
        
        return(messageserver);
        
    }
    
    //String: adicionar_user_balancoaadicionar
    //Devolve SUCESSO ou INSUCESSO
    public String adicionarBalanco(String user,String balanco) throws IOException{
        
        String mensagem = "adicionar_"+user+"_"+balanco;
        
        OutputStream os = this.serversocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(mensagem+"\n");
        bw.flush();
        //System.out.println("Messagem enviada: "+mensagem);
        
        //Receber mensagem do servidor
        InputStream is = this.serversocket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        
        String messageserver = br.readLine();
        //System.out.println("Messagem recebida do servidor: " +messageserver);
        
        return(messageserver);
        
    }
    
    //String: transferir_utilizador1_utilizador2_quantidade_token
    //Resposta: INSUCESSO ou b1_b2 (onde b1 e b2 são os balanços resultados da 
    //transferência)
    public String transferir(String user1, String user2, String quantidade, String token) throws IOException{
        
        String mensagem = "transferir_"+user1+"_"+user2+"_"+quantidade+"_"+token;
        
        OutputStream os = this.serversocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(mensagem+"\n");
        bw.flush();
        //System.out.println("Messagem enviada: "+mensagem);
        
        //Receber mensagem do servidor
        InputStream is = this.serversocket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        
        String messageserver = br.readLine();
        //System.out.println("Messagem recebida do servidor: " +messageserver);
        
        return(messageserver);
        
    }
    
    
    //String: token_user
    //Resposta: INSUCESSO ou token
    public String getToken(String user) throws IOException{
        
        String mensagem = "token_"+user;
        
        OutputStream os = this.serversocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(mensagem+"\n");
        bw.flush();
        //System.out.println("Messagem enviada: "+mensagem);
        
        //Receber mensagem do servidor
        InputStream is = this.serversocket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        
        String messageserver = br.readLine();
        //System.out.println("Messagem recebida do servidor: " +messageserver);
        
        return(messageserver);
        
    }
    
}
