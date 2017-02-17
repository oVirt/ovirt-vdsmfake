package org.ovirt.vdsmfake.rpc.json;

import static com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE;

import java.util.function.Consumer;

import org.ovirt.vdsm.jsonrpc.client.JsonRpcRequest;
import org.ovirt.vdsm.jsonrpc.client.JsonRpcResponse;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class HystrixCommandExecutor implements CommandExecutor {

    private CommandExecutor defaultCommandExecutor = new DefaultCommandExecutor();

    @Override
    public JsonRpcResponse process(JsonRpcRequest request) {
        HystrixCommand.Setter setter = setter(request.getMethod() + ".Prepare");
        final HystrixCommand<JsonRpcResponse> preparationCommand = new HystrixCommand<JsonRpcResponse>(setter) {
            @Override
            protected JsonRpcResponse run() throws Exception {
                return defaultCommandExecutor.process(request);
            }
        };
        return preparationCommand.execute();
    }

    @Override
    public Consumer<JsonRpcResponse> aroundConsume(Consumer<JsonRpcResponse> responseConsumer, String method) {
        return (response) -> {
            HystrixCommand.Setter setter = setter(method + ".Send");

            final HystrixCommand<Object> sendCommand = new HystrixCommand<Object>(setter) {

                @Override
                protected Object run() throws Exception {
                    responseConsumer.accept(response);
                    return null;
                }
            };
            sendCommand.execute();
        };
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

}
