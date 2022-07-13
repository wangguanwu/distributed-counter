package org.gw.raft.rpc;

import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import org.gw.raft.CounterClosure;
import org.gw.raft.CounterService;

/**
 * @author guanwu
 * @created on 2022-07-13 16:19:09
 **/
public class IncrementAndGetRequestProcessor implements RpcProcessor<IncrementAndGetRequest> {
    private final CounterService counterService;
    public IncrementAndGetRequestProcessor(CounterService counterService) {
        this.counterService = counterService;
    }

    @Override
    public void handleRequest(RpcContext rpcCtx, IncrementAndGetRequest request) {
        final CounterClosure closure = new CounterClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getValueResponse());
            }
        };
        this.counterService.incrementAndGet(request.getDelta(), closure);
    }

    @Override
    public String interest() {
        return IncrementAndGetRequest.class.getName();
    }
}
