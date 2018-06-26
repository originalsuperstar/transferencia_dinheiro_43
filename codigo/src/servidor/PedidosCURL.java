package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
//Este import a seguir tem de ser descarregado e adicionado à biblioteca:
//link usado: http://www.java2s.com/Code/Jar/j/Downloadjsonsimple11jar.htm
import org.json.simple.JSONObject;

public class PedidosCURL {
    
    public static String insereUserNaOrg(String user,String org) throws MalformedURLException, IOException{
                        
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://localhost:4000/users").openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpcon.setRequestMethod("POST");
            httpcon.connect();
            
            byte[] outputBytes = ("username="+user+"&orgName="+org).getBytes("UTF-8");
            OutputStream os = httpcon.getOutputStream();
            os.write(outputBytes);
            os.close();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }
            
            instr.close();
            br.close();
            
            return finalr;
            
        }
        
        public static String getToken(String response){
            Boolean found = false;
            for(int i =0; ((i<response.length())&&(!found)); i++){
                if (response.charAt(i)=='\"') {
                    if ((response.charAt(i+1)!=',')&&(response.charAt(i+1)!=':')&&(response.charAt(i+1)!='}')&&(response.charAt(i-1)!=':')){
                        String aux = "";
                        for(int j=i+1; (response.charAt(j)!='\"'); j++){
                            aux+=response.charAt(j);
                        }
                        if(aux.equals("token")){
                            found=true;
                            String aux2 = "";
                            i+=9;
                            for(int j=i; (response.charAt(j)!='\"'); j++){
                                aux2+=response.charAt(j);
                            }
                            
                            return aux2;
                            
                        }
                    }
                }
            }
            return response;
        }
        
        public static String criarCanal(String token,String canal) throws MalformedURLException, IOException{
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://localhost:4000/channels").openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();
            
            //byte[] outputBytes = ("{channelName:"+canal+",channelConfigPath:../artifacts/channel/"+canal+".tx}").getBytes("UTF-8");
            JSONObject value = new JSONObject();
            value.put("channelName", canal);
            value.put("channelConfigPath","../artifacts/channel/mychannel.tx");
            byte[] outputBytes = value.toString().getBytes("UTF-8");
            
            OutputStream os = httpcon.getOutputStream();
            os.write(outputBytes);
            os.close();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }
            
            return finalr;
            
        }
        
        public static String joinCanal(String token,String canal, String[] peers) throws MalformedURLException, IOException{

            HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://localhost:4000/channels/"+canal+"/peers").openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();
            
            String smile = "{\"peers\":[";
            for(int i=0; i<peers.length;i++){
                smile+=("\""+peers[i]+"\"");
                if(i!=peers.length-1){
                    smile+=",";
                }
            }
            smile+="]}";
            
            byte[] outputBytes = smile.getBytes("UTF-8");
            
            OutputStream os = httpcon.getOutputStream();
            os.write(outputBytes);
            os.close();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }
            
            return finalr;
        }
        
        public static String instalarChaincode(String token,String[] peers, String versao) throws MalformedURLException, ProtocolException, IOException{
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://localhost:4000/chaincodes").openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();
            
            //Transforma array de string para o formato correto
            //array = [s1,s2,s3]
            //String final = ["s1","s2","s3"] (escrevem-se os parentesis e as aspas)
            String peersString = "[";
            for(int i=0; i<peers.length; i++){
                peersString+="\""+peers[i]+"\"";
                if(i!=peers.length-1){
                    peersString+=",";
                }
            }
            peersString+="]";
            
            String smile = "{\"peers\":"+peersString;
            smile+=",\"chaincodeName\":\"mycc\"";
            smile+=",\"chaincodePath\":\"github.com/example_cc\"";
            smile+=",\"chaincodeVersion\":\""+versao+"\"}";
            
            byte[] outputBytes = smile.getBytes("UTF-8");
            
            
            
            OutputStream os = httpcon.getOutputStream();
            os.write(outputBytes);
            os.close();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }
            
            return finalr;
        }
        
        public static String instantiateChaincode(String token,String[] args, String versao) throws MalformedURLException, IOException{
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://localhost:4000/channels/mychannel/chaincodes").openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();
            
            String argsString = "[";
            for(int i=0; i<args.length; i++){
                argsString+="\""+args[i]+"\"";
                if(i!=args.length-1){
                    argsString+=",";
                }
            }
            argsString+="]";
            
            String smile ="{\"chaincodeName\":\"mycc\"";
            smile+=",\"chaincodeVersion\":\""+versao+"\"";
            smile+=",\"args\":"+argsString+"}";
            
            //System.out.println(smile);
            
            byte[] outputBytes = smile.getBytes("UTF-8");
            
            OutputStream os = httpcon.getOutputStream();
            os.write(outputBytes);
            os.close();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }
            
            //System.out.println(smile);
            
            return finalr;
        }
        
        public static String invokeRequest_move(String token,String a,String b,String x) throws MalformedURLException, ProtocolException, IOException{
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://localhost:4000/channels/mychannel/chaincodes/mycc").openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();
            
            String argsString = "[\""+a+"\",\""+b+"\",\""+x+"\"]";
            String smile = "{\"fcn\":\"move\",\"args\":"+argsString+"}";
            //System.out.println(smile);
            
            byte[] outputBytes = smile.getBytes("UTF-8");
            
            OutputStream os = httpcon.getOutputStream();
            os.write(outputBytes);
            os.close();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }
            
            
            return finalr;
        }
        
        public static String chaincodeQuery(String token,String peer,String func,String args) throws MalformedURLException, IOException{
            
            String newArgs = "";
            for(int i=0; i<args.length(); i++){
                if(args.charAt(i)=='\"'){
                    newArgs+="%22";
                }
                else if (args.charAt(i)=='['){
                    newArgs+="%5B";
                }
                else if (args.charAt(i)==']'){
                    newArgs+="%5D";
                }
                else{
                    newArgs+=args.charAt(i);
                }
            }
            String urlAux ="peer="+peer+"&fcn="+func+"&args="+newArgs;
            String url = "http://localhost:4000/channels/mychannel/chaincodes/mycc?"+urlAux;
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("GET");
            httpcon.connect();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }

            return finalr;
        }
        
        public static String queryBlockByBlockNumber(String token,String block,String peer) throws MalformedURLException, IOException{
            
            String urlAux =block+"?peer="+peer;
            String url = "http://localhost:4000/channels/mychannel/blocks/"+urlAux;
            
            //System.out.println(url);
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("GET");
            httpcon.connect();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }

            return finalr;
        }
        
        public static String queryTransactionByTransactionID(String token,String trx_id,String peer) throws MalformedURLException, IOException{
            
            String urlAux =trx_id+"?peer="+peer;
            //String urlAux ="TRX_ID"+"?peer="+peer;
            String url = "http://localhost:4000/channels/mychannel/transactions/"+urlAux;
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("GET");
            httpcon.connect();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }

            return finalr;
        }

        public static String queryChainInfo(String token,String peer) throws MalformedURLException, IOException{

            String urlAux ="?peer="+peer;
            String url = "http://localhost:4000/channels/mychannel"+urlAux;
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("GET");
            httpcon.connect();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }

            return finalr;
        }
        
        public static String queryInstalledChaincodes(String token,String peer) throws MalformedURLException, IOException{
        
            String urlAux ="?peer="+peer+"&type=installed";
            String url = "http://localhost:4000/chaincodes"+urlAux;
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("GET");
            httpcon.connect();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }

            return finalr;
        }
        
        public static String queryInstantiatedChaincodes(String token,String peer) throws MalformedURLException, IOException{

            String urlAux ="?peer="+peer+"&type=instantiated";
            String url = "http://localhost:4000/chaincodes"+urlAux;
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("GET");
            httpcon.connect();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }

            return finalr;
        }
        
        public static String queryChannels(String token,String peer) throws MalformedURLException, IOException{

            String urlAux ="?peer="+peer;
            String url = "http://localhost:4000/channels"+urlAux;
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("GET");
            httpcon.connect();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }

            return finalr;
        }
        
        public static String invokeRequest_set(String token,String a,String v) throws MalformedURLException, ProtocolException, IOException{
            
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://localhost:4000/channels/mychannel/chaincodes/mycc").openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("authorization", "Bearer "+token);
            httpcon.setRequestProperty("content-type", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();
            
            String argsString = "[\""+a+"\",\""+v+"\"]";
            String smile = "{\"fcn\":\"set\",\"args\":"+argsString+"}";
            System.out.println(smile);
            
            byte[] outputBytes = smile.getBytes("UTF-8");
            
            OutputStream os = httpcon.getOutputStream();
            os.write(outputBytes);
            os.close();
            
            InputStream instr = httpcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(instr));
            String lin;
            String finalr = ""; 
            while((lin = br.readLine())!=null){
               finalr+=lin;
            }
            
            
            return finalr;
        }
    
}
