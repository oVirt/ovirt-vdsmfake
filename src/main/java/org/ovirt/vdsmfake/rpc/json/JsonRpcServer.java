package org.ovirt.vdsmfake.rpc.json;

import static com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.ovirt.vdsm.jsonrpc.client.ClientConnectionException;
import org.ovirt.vdsm.jsonrpc.client.JsonRpcRequest;
import org.ovirt.vdsm.jsonrpc.client.JsonRpcResponse;
import org.ovirt.vdsm.jsonrpc.client.ResponseBuilder;
import org.ovirt.vdsm.jsonrpc.client.reactors.Reactor;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorClient;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorClient.MessageListener;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorFactory;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorListener;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorType;
import org.ovirt.vdsmfake.AppConfig;
import org.ovirt.vdsmfake.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.servo.publish.BasicMetricFilter;
import com.netflix.servo.publish.MetricObserver;
import com.netflix.servo.publish.MonitorRegistryMetricPoller;
import com.netflix.servo.publish.PollRunnable;
import com.netflix.servo.publish.PollScheduler;
import com.netflix.servo.publish.graphite.GraphiteMetricObserver;

public class JsonRpcServer {
    private static final Logger log = LoggerFactory
            .getLogger(JsonRpcServer.class);

    private final static int TIMEOUT = 5000;
    private static ReactorListener listener;
    private int jsonPort;
    private boolean encrypted;
    private String hostName;
    private static final ConcurrentHashMap<String, ReactorClient> clientsMap = new ConcurrentHashMap<>();
    private final String eventSupportedMethods = AppConfig.getInstance().getEventSupportedMethods().toString();
    private ExecutorService service = Executors.newFixedThreadPool(AppConfig.getInstance().getJsonThreadPoolSize(), new BasicThreadFactory.Builder()
            .namingPattern("jsonrpcserver-pool-%d")
            .daemon(true)
            .priority(Thread.MAX_PRIORITY)
            .build());

    public JsonRpcServer(String hostName, int jsonPort, boolean encrypted) {
        this.hostName = hostName;
        this.jsonPort = jsonPort;
        this.encrypted = encrypted;
    }

    public void setReactorsMap(String vmId, ReactorClient client){
        clientsMap.put(vmId, client);
    }

    public static void removeClientByVmId(String vmId){
        clientsMap.remove(vmId);
    }

    public static ReactorClient getClientByVmId(String vmId){
        return clientsMap.get(vmId);
    }

    public void start() {
        try {
            String hostName = System.getProperty("fake.host");

            if (hostName == null) {
                hostName = "::";
            }
            log.debug("Opening a Stomp server " + hostName + ":" + jsonPort);
            Reactor reactor;

            if (!encrypted) {
                reactor = ReactorFactory.getReactor(null, ReactorType.STOMP);
            } else {
                reactor = ReactorFactory.getReactor(new VdsmProvider(AppConfig.getInstance().getCertspath(), log),
                        ReactorType.STOMP);
            }

            final Future<ReactorListener> futureListener =
                    reactor.createListener(hostName, jsonPort, new ReactorListener.EventListener() {

                        @Override
                        public void onAcccept(final ReactorClient client) {
                            log.info("Accepting connection from " + client.getHostname());
                            client.addEventListener(new MessageListener() {
                                // you can provide your implementation of MessageListener
                                @Override
                                public void onMessageReceived(byte[] message) {
                                    MessageHandler handler = new MessageHandler(client, message);
                                    // handler.run();
                                    service.submit(handler);
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

    private class MessageHandler implements Runnable {
        private ReactorClient client;
        private byte[] message;

        public MessageHandler(ReactorClient client, byte[] message) {
            super();
            this.client = client;
            this.message = message;
        }

        public void run() {
            JsonRpcRequest request = null;
            try {
                request = JsonRpcRequest.fromByteArray(message);

                //store clients for events usage
                if(eventSupportedMethods.contains(request.getMethod())){
                    setReactorsMap(request.getParams().get("vmID").asText(), client);
                }

                ContextHolder.init();
                if (client.getRetryPolicy().getIdentifier() != null) {
                    ContextHolder.setServerName(client.getRetryPolicy().getIdentifier());
                    log.debug("client policy identifier {}", client.getRetryPolicy().getIdentifier());
                }
                else if (client.getHostname() != null) {
                    ContextHolder.setServerName(client.getHostname());
                }
                else{
                    ContextHolder.setServerName(Integer.toString(client.hashCode()));
                    log.error("client identifier were not found, using hash");
                }

                HystrixCommand.Setter setter = setter(request.getMethod() + ".Prepare");
                final JsonRpcRequest finalRequest = request;
                final HystrixCommand<ResponseBuilder> preparationCommand = new HystrixCommand<ResponseBuilder>(setter) {

                    @Override protected ResponseBuilder run() throws Exception {
                        ResponseBuilder builder = new ResponseBuilder(finalRequest.getId());
                        CommandFactory.createCommand(finalRequest.getMethod()).run(finalRequest.getParams(),
                                builder);
                        return builder;
                    }
                };
                final ResponseBuilder builder = preparationCommand.execute();
                setter = setter(request.getMethod() + ".Send");

                final HystrixCommand<Object> sendCommand = new HystrixCommand<Object>(setter) {

                    @Override protected Object run() throws Exception {
                        send(builder.build(), finalRequest.getMethod());
                        return null;
                    }
                };
                sendCommand.execute();
            } catch (Throwable e) {
                log.error("Failure in processing request", e);
                Map<String, Object> error = new HashMap<>();
                error.put("code", 100);
                error.put("message", e.getMessage());

                send(new ResponseBuilder(request.getId()).withError(error).build(), request.getMethod());
            }
        }

        private void send(JsonRpcResponse response, String method) {
            if (log.isDebugEnabled()) {
                log.debug("Request is " + method + " got response " + new String(response.toByteArray()));
            }

            try {
                client.sendMessage(response.toByteArray());
            } catch (ClientConnectionException e) {
                // if error we are unable to talk it is better to propagate
                log.error("Error in sending", e);
                client.close();
            }
        }
    }

    private HystrixCommand.Setter setter(final String key) {
        return HystrixCommand.Setter.withGroupKey(
                HystrixCommandGroupKey.Factory.asKey(key)
        ).andCommandKey(
                HystrixCommandKey.Factory.asKey(key)
        ).andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(SEMAPHORE)
                        .withExecutionTimeoutEnabled(false)
                        .withCircuitBreakerEnabled(false)
                        .withFallbackEnabled(false)
                        .withMetricsRollingStatisticalWindowInMilliseconds(60000)
                        .withMetricsRollingStatisticalWindowBuckets(60)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(300)
        );
    }

    public static void initMonitoring() {
        HystrixPlugins.getInstance().registerMetricsPublisher(HystrixServoMetricsPublisher.getInstance());

        // Minimal Servo configuration for publishing to Graphite
        final List<MetricObserver> observers = new ArrayList<MetricObserver>();

        String graphiteUrl = System.getProperty("graphite.url");
        int graphiteInterval = Integer.getInteger("graphite.interval", 15);
        if (graphiteUrl != null) {
            observers.add(new GraphiteMetricObserver("vdsmfake", graphiteUrl));
            PollScheduler.getInstance().start();
            PollRunnable task =
                    new PollRunnable(new MonitorRegistryMetricPoller(), BasicMetricFilter.MATCH_ALL, true, observers);
            PollScheduler.getInstance().addPoller(task, graphiteInterval, TimeUnit.SECONDS);
        }
    }

}
