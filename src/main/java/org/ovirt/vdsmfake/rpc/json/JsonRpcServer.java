package org.ovirt.vdsmfake.rpc.json;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.ovirt.vdsm.jsonrpc.client.JsonRpcRequest;
import org.ovirt.vdsm.jsonrpc.client.JsonRpcResponse;
import org.ovirt.vdsm.jsonrpc.client.ResponseBuilder;
import org.ovirt.vdsm.jsonrpc.client.reactors.Reactor;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorClient;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorClient.MessageListener;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorFactory;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorListener;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorType;
import org.ovirt.vdsmfake.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonRpcServer {
    private static final Logger log = LoggerFactory
            .getLogger(JsonRpcServer.class);

    private final static int TIMEOUT = 5000;
    private static ReactorListener listener;
    private int jsonPort;
    private boolean encrypted;

    public JsonRpcServer(int jsonPort, boolean encrypted) {
        this.jsonPort = jsonPort;
        this.encrypted = encrypted;
    }

    public void start() {
        try {

            String hostName = System.getProperty("fake.host");

            if (hostName == null) {
                hostName = "localhost";
            }

            log.debug("Opening a Stomp server " + hostName + ":" + jsonPort);
            Reactor reactor;

            if (!encrypted) {
                reactor = ReactorFactory.getReactor(null, ReactorType.STOMP);
            } else {
                reactor = ReactorFactory.getReactor(new FakeVDSMSSLProvider(System.getProperty("fake.keystore"),
                        System.getProperty("fake.truststore"), "changeit", null), ReactorType.STOMP);
            }

            final Future<ReactorListener> futureListener =
                    reactor.createListener(hostName, jsonPort, new ReactorListener.EventListener() {

                        @Override
                        public void onAcccept(final ReactorClient client) {
                            client.addEventListener(new MessageListener() {
                                // you can provide your implementation of MessageListener
                                @Override
                                public void onMessageReceived(byte[] message) {
                                    try {
                                        JsonRpcRequest request =
                                                JsonRpcRequest.fromByteArray(message);

                                        ContextHolder.init();
                                        ContextHolder.setServerName(client.getHostname());
                                        ResponseBuilder builder = new ResponseBuilder(request.getId());
                                        String methodName = request.getMethod();
                                        builder =
                                                CommandFactory.createCommand(methodName).run(request.getParams(),
                                                        builder);
                                        JsonRpcResponse response = builder.build();
                                        log.info("Request is " + request.getMethod() + " got response "
                                                + new String(response.toByteArray()));
                                        client.sendMessage(response.toByteArray());
                                    } catch (Throwable e) {
                                        log.error("Failure in processing request", e);
                                    }
                                }


                            });

                        }
                    });

            listener = futureListener.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Failure to start json server socket", e);
        }
    }

    public static void shutdown() {
        listener.close();
    }
}
