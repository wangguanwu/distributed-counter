package org.gw.raft;

import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.error.RemotingException;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.InvokeCallback;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import org.gw.raft.rpc.CounterGrpcHelper;
import org.gw.raft.rpc.IncrementAndGetRequest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

public class CounterClient {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage : java com.alipay.sofa.jraft.example.counter.CounterClient {groupId} {conf}");
            System.out
                    .println("Example: java com.alipay.sofa.jraft.example.counter.CounterClient counter 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083");
            System.exit(1);
        }
        final String groupId = args[0];
        final String confStr = args[1];
        CounterGrpcHelper.initGRpc();
        final Configuration configuration = new Configuration();
        if (!configuration.parse(confStr)) {
            throw new IllegalArgumentException("Fail to parse conf:" + confStr);
        }

        RouteTable.getInstance().updateConfiguration(groupId, configuration);
        final CliClientServiceImpl cliClientService = new CliClientServiceImpl();
        cliClientService.init(new CliOptions());
        if (!RouteTable.getInstance().refreshLeader(cliClientService, groupId, 1000).isOk()) {
            throw new IllegalStateException("Refresh Leader failed");
        }
        final PeerId leader = RouteTable.getInstance().selectLeader(groupId);

        System.out.println("Leader is " + leader);
        final int n = 1000;
        final CountDownLatch latch = new CountDownLatch(n);
        final long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            incrementAndGet(cliClientService, leader, i, latch);
        }
        latch.await();
        System.out.println("n + ops, cost: " + (System.currentTimeMillis() - start) + "ms");
        System.exit(0);
    }

    private static void incrementAndGet(CliClientServiceImpl cliClientService, PeerId leader, int delta, CountDownLatch latch) throws RemotingException, InterruptedException {
        IncrementAndGetRequest request = IncrementAndGetRequest.newBuilder()
                .setDelta(delta)
                .build();
        cliClientService.getRpcClient().invokeAsync(leader.getEndpoint(), request, new InvokeCallback() {

            @Override
            public void complete(Object result, Throwable throwable) {
                if(throwable == null) {
                    latch.countDown();
                    System.out.println("incrementAndGet result: " + result);
                } else {
                    throwable.printStackTrace();
                    latch.countDown();
                }
            }

            @Override
            public Executor executor() {
                return null;
            }
        }, 5000);
    }


}