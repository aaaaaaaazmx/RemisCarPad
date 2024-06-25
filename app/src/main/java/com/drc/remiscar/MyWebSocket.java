package com.drc.remiscar;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class MyWebSocket {
    private WebSocketClient webSocketClient;
    private final String data;
    private boolean bl = false;
    private boolean bl2 = false;
    public MyWebSocket(String data) {
        this.data = data;
        webSocket(data);
    }
    private void webSocket(final String data){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    URI serverURI = URI.create("ws://"+DetailActivity._ServerIP+":8001/gskj-pushServer/websocket/666666/1729804602932461824/admin/395/0");
                    webSocketClient = new WebSocketClient(serverURI) {
                        //打开连接
                        @Override
                        public void onOpen(ServerHandshake handshakedata) {
                            System.out.println("WebSocket:::onOpen:");
                            if (bl2)
                                sendData(data);
                        }
                        //服务端返回消息
                        @Override
                        public void onMessage(String message) {
                            //System.out.println("WebSocket:::onMessage:"+message);
                            listener.getMessage(message);
                            bl = false;
                        }
                        //关闭连接
                        @Override
                        public void onClose(int code, String reason, boolean remote) {
                            System.out.println("WebSocket:::onClose:"+code);
                            if (!bl){
                                listener.closeWebSocket(code,reason,remote);
                                bl = true;
                            }
                            bl2 = false;
                        }
                        //出现异常
                        @Override
                        public void onError(Exception ex) {
                            String str = ex.fillInStackTrace().toString();
                            System.out.println("WebSocket:::onError:"+str);
                            if (!bl){
                                listener.closeWebSocket(-1,str,false);
                                bl = true;
                            }
                            bl2 = false;
                        }
                    };
                    //webSocketClient.connectBlocking();
                    reConnect();
                    sendData(data);
                }catch (Exception e){
                    System.out.println("WebSocket:::"+e.fillInStackTrace());
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (true){
                    //webSocket连接断开后一直重复尝试连接直到连接成功为止
                    if (webSocketClient != null&&webSocketClient.isClosed()) {
                        reConnect();
                        if (!bl2)
                            bl2 = true;
                    }
                }
            }
        }.start();
    }
    //断线重连方法
    private void reConnect(){
        try{
            if (webSocketClient==null)
                return;
            if (webSocketClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                webSocketClient.connectBlocking();
            } else if (webSocketClient.getReadyState().equals(ReadyState.CLOSING)
                    || webSocketClient.getReadyState().equals(ReadyState.CLOSED)) {
                webSocketClient.reconnectBlocking();
            }
            Thread.sleep(5000);
        }catch (Exception e){
            System.out.println("WebSocket:::reConnect:"+e.fillInStackTrace());
        }
    }
    public void sendData(String data){
        //防止WebsocketNotConnectedException
        while (!webSocketClient.getReadyState().equals(ReadyState.OPEN)){
            System.out.equals("WebSocket:::连接断开了");
        }
        if (webSocketClient != null&&!webSocketClient.isClosed()) {
            webSocketClient.send(data);
        }
    }
    public boolean isClosed(){
        if (webSocketClient != null) {
            return webSocketClient.isClosed();
        }
        return false;
    }
    public void closeWebSocket(){
        if (webSocketClient!=null){
            webSocketClient.close();
            webSocketClient = null;
        }
    }

    WebSocketListener listener;
    public void setWebSocket(WebSocketListener listener){
        this.listener = listener;
    }
    public interface WebSocketListener{
        void getMessage(String message);
        void closeWebSocket(int code, String reason, boolean remote);
    }
}