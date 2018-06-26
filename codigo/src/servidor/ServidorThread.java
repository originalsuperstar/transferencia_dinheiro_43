package servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorThread extends Thread{
    
    protected Socket clientsocket;
    private List<String> users;
    private List<String> userspasswords;
    private List<String> userstoken;
    private List<String> balancos;
    private Boolean free;

    public ServidorThread(Socket clientSocket, List<String> users, List<String> userspasswords, List<String> userstoken, List<String> balancos, Boolean free) {
        this.clientsocket = clientSocket;
        this.users = users;
        this.userspasswords = userspasswords;
        this.userstoken = userstoken;
        this.balancos = balancos;
        this.free = free;
    }
    public void run(){
        
        Boolean sair = false;
        String resposta = "";
        
        while(!sair){
            
            //Receber mensagem do cliente
            InputStream is = null;
            try {
                is = clientsocket.getInputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String message = null;
            try {
                message = br.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Message recebida do cliente: "+message);
        
            String comando = comandoString(message);
            
            //Aqui vão ser implementadas as coisas da blockchain!!!!
            //Registo!
            //String: registar_nome_password_balanco
            if(comando.equals("registar")){
                
                registarUtilizador(message);
                
            }
            
            //Balanço!
            //String: balanco_user
            else if(comando.equals("balanco")){
                
                getBalanco(message);
                        
            }
            
            //Adicionar Balanço!
            ////String: adicionar_user_balancoaadicionar
            else if(comando.equals("adicionar")){
                
                addBalanco(message);
                        
            }
            
            //Login!
            //String: login_nome_password_token
            //O servidor vai devolver sucesso ou insucesso???
            //Equivalente a juntar-se ao canal???
            else if(comando.equals("login")){
                
                try {
                    loginUtilizador(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
            //Get Token do user
            //String: token_user
            //Resposta: INSUCESSO ou token
            else if(comando.equals("token")){
                
                getToken(message);
                
            }
            
            //Transferir!
            //String: transferir_utilizador1_utilizador2_balanco1_balanco2_quantidade_token
            //Aqui é que vamos reiniciar a rede! Logo antes de iniciar esta função!!
            else if(comando.equals("transferir")){
                
                try {
                    transferir(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            else if(comando.equals("SAIR")){
                sair=true;
                try {
                    this.clientsocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
            
                //Enviar mensagem ao cliente
                OutputStream os = null;
                try {
                    os = clientsocket.getOutputStream();
                } catch (IOException ex) {
                    Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
            
                try {
                    System.out.println("Mensagem enviada: Comando inválido!\n");
                    bw.write("COMANDO INVÁLIDO\n");
                } catch (IOException ex) {
                    Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    bw.flush();
                } catch (IOException ex) {
                    Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
        }
            
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
    //Envia o token para o utilizador ou INSUCESSO
    public void registarUtilizador(String message){
        
        String registonome = "";
        String registopass = "";
        String balanco = "";
        
        PedidosCURL pedidos = new PedidosCURL();
                
        for(int i=9; message.charAt(i)!='_'; i++){
            registonome+=message.charAt(i);
        }
        for(int i=(10+registonome.length());message.charAt(i)!='_'; i++){
            registopass+=message.charAt(i);
        }
        for(int i=(11+registonome.length()+registopass.length());i<message.length(); i++){
            balanco+=message.charAt(i);
        }
                
        Boolean existe = this.users.contains(registonome);
        
        if(existe){
            
            //Enviar mensagem INSUCESSO visto que o utilizador já existe!
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write("INSUCESSO"+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        else{
            
            //Enviar token (e comunicar com a rede para saber qual é)
            try {
                String resposta = pedidos.insereUserNaOrg(registonome,"org1");
                String token = pedidos.getToken(resposta);
                
                while(!this.free){
                    
                }
                this.free=false;
                   
                System.out.println("Token: "+token);
                    
                //Enviar mensagem ao cliente
                OutputStream os = null;
                try {
                    os = clientsocket.getOutputStream();
                } catch (IOException ex) {
                    Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
            
                try {
                    bw.write(token+"\n");
                    this.users.add(registonome);
                    this.userspasswords.add(registopass);
                    this.userstoken.add(token);
                    this.balancos.add(balanco);
                    this.free=true;
                } catch (IOException ex) {
                    Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    bw.flush();
                } catch (IOException ex) {
                    Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    //String: login_nome_password_token
    //Envia SUCESSO ou INSUCESSO para o cliente
    public void loginUtilizador(String message) throws IOException{
        
        String nome = "";
        String pass = "";
        String token = "";
        
        PedidosCURL pedidos = new PedidosCURL();
        
        for(int i=6; message.charAt(i)!='_'; i++){
            nome+=message.charAt(i);
        }
        for(int i=(7+nome.length()); message.charAt(i)!='_'; i++){
            pass+=message.charAt(i);
        }
        for(int i=8+nome.length()+pass.length(); i<message.length(); i++){
            token+=message.charAt(i);
        }
        
        Boolean found = false;
        Boolean valido = true;
        int useri=0;
        for(int i=0; ((i<this.users.size())&&(!found)); i++){
            if(this.users.get(i).equals(nome)){
                found=true;
                useri=i;
            }
        }
        if((!this.userspasswords.get(useri).equals(pass))){
            valido=false;
        }
        
        if(!valido){
            
            //Enviar mensagem INSUCESSO!
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write("INSUCESSO"+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{
            
            //Juntar ao canal
            while(!this.free){
                
            }
            this.free=false;
            String resposta = pedidos.insereUserNaOrg(nome,"org1");
            String tokenuser = pedidos.getToken(resposta);
            //String[] peers = {"peer1","peer2"};
            //pedidos.joinCanal(tokenuser,"mychannel",peers);
            this.free=true;
            
            //Enviar mensagem SUCESSO!
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write("SUCESSO"+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    //String: transferir_utilizador1_utilizador2_quantidade_token
    //Reiniciar a rede antes de correr isto!
    //Envia para o cliente uma string INSUCESSO ou: b1_b2 (onde b1 e b2 é o balanço
    //do utilizador 1 e 2 após a transação)
    public void transferir(String mensagem) throws IOException, InterruptedException{
        
        String user1 = "";
        String user2 = "";
        String b1 = "";
        String b2 = "";
        String quantidade = "";
        String token = "";
        
        for(int i=11; mensagem.charAt(i)!='_'; i++){
            user1+=mensagem.charAt(i);
        }
        for(int i=12+user1.length();mensagem.charAt(i)!='_'; i++){
            user2+=mensagem.charAt(i);
        }
        for(int i=13+user1.length()+user2.length();mensagem.charAt(i)!='_'; i++){
            quantidade+=mensagem.charAt(i);
        }
        for(int i=14+user1.length()+user2.length()+quantidade.length();i<mensagem.length(); i++){
            token+=mensagem.charAt(i);
        }
        
        Boolean valido = true;
        
        Boolean found1 = false;
        int user1i = -1;
        Boolean found2 = false;
        int user2i = -1;
        for(int i=0; ((i<this.users.size())&&((!found1)||(!found2))); i++){
            if(this.users.get(i).equals(user1)){
                found1=true;
                user1i=i;
                while(!this.free){
                    
                }
                b1=this.balancos.get(user1i);
                
            }
            if(this.users.get(i).equals(user2)){
                found2=true;
                user2i=i;
                b2=this.balancos.get(user2i);
            }
        }
        if(!found1||!found2){
            valido = false;
        }
        if(Integer.parseInt(b1)<Integer.parseInt(quantidade)){
            valido=false;
        }
        if(user1.equals(user2)){
            valido=false;
        }
        
        if(!valido){
            
            //Enviar mensagem INSUCESSO visto que o utilizador já existe!
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write("INSUCESSO"+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        else{
            
            PedidosCURL p = new PedidosCURL();
            
            String response = p.insereUserNaOrg("admin","org1");
            String admintoken = p.getToken(response);
            response = p.criarCanal(admintoken,"mychannel");
            Thread.sleep(5000);
            String[] peers = {"peer1","peer2"};
            response = p.joinCanal(admintoken,"mychannel", peers);
            //System.out.println(response);
            response = p.instalarChaincode(admintoken,peers,"v0");
            //System.out.println(response);
            String[] argsAux = {user1,b1,user2,b2};
            response = p.instantiateChaincode(admintoken,argsAux,"v0");
            //System.out.println(response);
            //System.out.println(user1+": "+p.chaincodeQuery(admintoken,"peer1","query","[\""+user1+"\"]"));
            //System.out.println(user2+": "+p.chaincodeQuery(admintoken,"peer1","query","[\""+user2+"\"]"));
            p.invokeRequest_move(admintoken,user1,user2,quantidade);
            String r1 = p.chaincodeQuery(admintoken,"peer1","query","[\""+user1+"\"]");
            String r2 = p.chaincodeQuery(admintoken,"peer1","query","[\""+user2+"\"]");
            String bf1 = "";
            String bf2 = "";
            for(int i=user1.length()+9; r1.charAt(i)!=' '; i++){
                bf1+=r1.charAt(i);
            }
            for(int i=user2.length()+9; r2.charAt(i)!=' '; i++){
                bf2+=r2.charAt(i);
            }
            String enviar = bf1+"_"+bf2+"\n";
            while(!free){
                
            }
            free=false;
            this.balancos.set(user1i, bf1);
            this.balancos.set(user2i, bf2);
            free=true;
            
            //Enviar balancos finais ao cliente
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write(enviar);
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    //String: balanco_user
    public void getBalanco(String message){
        
        String user = "";
        for(int i=8; i<message.length(); i++){
            user+=message.charAt(i);
        }
        
        Boolean found = false;
        int useri=-1;
        for(int i=0; ((i<this.users.size())&&(!found)); i++){
            if(this.users.get(i).equals(user)){
                found=true;
                useri=i;
            }
        }
        if(!found){
            //Enviar mensagem INSUCESSO visto que o utilizador não existe!
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write("INSUCESSO"+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write(this.balancos.get(useri)+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    //String: adicionar_user_balancoaadicionar
    //Devolve SUCESSO ou INSUCESSO
    public void addBalanco(String message){
        
        String user = "";
        String add = "";
        
        for(int i=10; message.charAt(i)!='_'; i++){
            user+=message.charAt(i);
        }
        for(int i=11+user.length(); i<message.length(); i++){
            add+=message.charAt(i);
        }
        
        Boolean found = false;
        int useri = -1;
        for(int i=0; ((i<this.users.size())&&(!found)); i++){
            if(this.users.get(i).equals(user)){
                found=true;
                useri=i;
            }
        }
        
        if(!found){
            //Enviar mensagem INSUCESSO visto que o utilizador não existe!
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write("INSUCESSO"+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            
            while(!this.free){
                
            }
            this.free=false;
            int novo = Integer.parseInt(this.balancos.get(useri))+Integer.parseInt(add);
            this.balancos.set(useri, String.valueOf(novo));
            this.free=true;
            
            //Enviar mensagem SUCESSO
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write("SUCESSO"+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    //Get Token do user
    //String: token_user
    //Resposta: INSUCESSO ou token
    public void getToken(String message){
        
        String user = "";
        for(int i=6; i<message.length(); i++){
            user+=message.charAt(i);
        }
        Boolean found = false;
        int useri = -1;
        for(int i=0; i<this.users.size(); i++){
            if(this.users.get(i).equals(user)){
                found=true;
                useri=i;
            }
        }
        
        if(!found){
            
            //Enviar mensagem INSUCESSO visto que o utilizador não existe!
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write("INSUCESSO"+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        else{
            
            //Enviar token
            OutputStream os = null;
            try {
                os = clientsocket.getOutputStream();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            try {
                bw.write(this.userstoken.get(useri)+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    
    
}
