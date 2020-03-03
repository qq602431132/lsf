package net.zfinfo.test;
import com.alibaba.fastjson.JSON;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class App extends NanoHTTPD {
    public App(int port) throws IOException {
        super(port);
    }
    public static void main(String[] args) {
        try {
            new App(8090).start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session){
        InputStream in= App.class.getResourceAsStream(session.getUri());
        String msg= null;
        String uri= session.getUri();
        if(uri.startsWith("/api")){
            Map map= new HashMap();
            map.put("like",1);
            map.put("this.",2);
            msg= JSON.toJSONString(map);
            return newFixedLengthResponse(msg);
        }else if(uri.startsWith("/view") || uri.startsWith("/static")){
            try {
                msg = inputStream2String(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newFixedLengthResponse(Response.Status.OK,getMimeTypeForFile(uri),msg);
        }
        try {
            return newFixedLengthResponse(Response.Status.OK, getMimeTypeForFile(uri), in,in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String inputStream2String(InputStream in)   throws   IOException   {
        StringBuffer out= new StringBuffer();
        byte[] b= new byte[4096];
        for(int n; (n = in.read(b))!=  -1;)   {
            out.append(new String(b, 0, n));
        }
        return   out.toString();
    }

}
