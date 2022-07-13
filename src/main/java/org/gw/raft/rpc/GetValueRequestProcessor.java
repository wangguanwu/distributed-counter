package org.gw.raft.rpc;

import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import org.gw.raft.CounterClosure;
import org.gw.raft.CounterService;

/**
 * @author guanwu
 * @created on 2022-07-13 15:47:03
 **/
public class GetValueRequestProcessor implements RpcProcessor<GetValueRequest> {
    private final CounterService counterService;
    public GetValueRequestProcessor(CounterService counterService) {
        this.counterService = counterService;
    }



    @Override
    public void handleRequest(RpcContext rpcCtx, GetValueRequest request) {
        final CounterClosure closure = new CounterClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getValueResponse());
            }
        };
        this.counterService.get(request.getReadOnlySafe(), closure);
    }

    @Override
    public String interest() {
        return GetValueRequest.class.getName();
    }
}
