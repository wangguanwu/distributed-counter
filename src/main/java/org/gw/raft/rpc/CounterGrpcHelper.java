package org.gw.raft.rpc;

import com.alipay.sofa.jraft.rpc.RpcServer;
import com.alipay.sofa.jraft.util.RpcFactoryHelper;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author guanwu
 * @created on 2022-07-13 14:55:44
 **/
public class CounterGrpcHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CounterGrpcHelper.class);

    public static RpcServer rpcServer;

    public static void initGRpc() {
        if ("com.alipay.sofa.jraft.rpc.impl.GrpcRaftRpcFactory".equals(RpcFactoryHelper.rpcFactory().getClass()
                .getName())) {
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(GetValueRequest.class.getName(),
                    GetValueRequest.getDefaultInstance());
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(
                    IncrementAndGetRequest.class.getName(),
                    IncrementAndGetRequest.getDefaultInstance());
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ValueResponse.class.getName(),
                    ValueResponse.getDefaultInstance());

            try {
                Class<?> clazz = Class.forName("com.alipay.sofa.jraft.rpc.impl.MarshallerHelper");
                Method registerRespInstance = clazz.getMethod("registerRespInstance", String.class, Message.class);
                registerRespInstance.invoke(null, GetValueRequest.class.getName(),
                        ValueResponse.getDefaultInstance());
                registerRespInstance.invoke(null, IncrementAndGetRequest.class.getName(),
                        ValueResponse.getDefaultInstance());
            } catch (Exception e) {
                LOG.error("Failed to init grpc server", e);
            }
        }
    }

    public static void setRpcServer(RpcServer rpcServer) {
        CounterGrpcHelper.rpcServer = rpcServer;
    }

    public static void blockUntilShutdown() {
        if (rpcServer == null) {
            return;
        }
        if ("com.alipay.sofa.jraft.rpc.impl.GrpcRaftRpcFactory".equals(RpcFactoryHelper.rpcFactory().getClass()
                .getName())) {
            try {
                Method getServer = rpcServer.getClass().getMethod("getServer");
                Object grpcServer = getServer.invoke(rpcServer);

                Method shutdown = grpcServer.getClass().getMethod("shutdown");
                Method awaitTerminationLimit = grpcServer.getClass().getMethod("awaitTermination", long.class,
                        TimeUnit.class);

                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            shutdown.invoke(grpcServer);
                            awaitTerminationLimit.invoke(grpcServer, 30, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                            e.printStackTrace(System.err);
                        }
                    }
                });
                Method awaitTermination = grpcServer.getClass().getMethod("awaitTermination");
                awaitTermination.invoke(grpcServer);
            } catch (Exception e) {
                LOG.error("Failed to block grpc server", e);
            }
        }
    }


}
