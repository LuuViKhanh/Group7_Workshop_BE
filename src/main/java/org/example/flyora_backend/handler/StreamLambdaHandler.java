package org.example.flyora_backend.handler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.InputStream;
import java.io.OutputStream;
import org.example.flyora_backend.FlyoraBackendApplication;
import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;


public class StreamLambdaHandler implements RequestStreamHandler {
    private SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    public StreamLambdaHandler() {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(FlyoraBackendApplication.class);
        } catch (ContainerInitializationException e) {
            throw new RuntimeException("Could not initialize Spring Boot Lambda handler", e);
        }
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws java.io.IOException {
        handler.proxyStream(input, output, context);
    }
}