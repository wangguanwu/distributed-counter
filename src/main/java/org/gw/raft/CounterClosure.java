package org.gw.raft;

import com.alipay.sofa.jraft.Closure;
import org.gw.raft.rpc.ValueResponse;

/**
 * @author guanwu
 * @created on 2022-07-12 17:21:34
 **/
public abstract class CounterClosure  implements Closure {
    private ValueResponse    valueResponse;
    private CounterOperation counterOperation;

    public void setCounterOperation(CounterOperation counterOperation) {
        this.counterOperation = counterOperation;
    }

    public CounterOperation getCounterOperation() {
        return counterOperation;
    }

    public ValueResponse getValueResponse() {
        return valueResponse;
    }

    public void setValueResponse(ValueResponse valueResponse) {
        this.valueResponse = valueResponse;
    }

    protected void failure(final String errorMsg, final String redirect) {
        final ValueResponse response = ValueResponse.newBuilder().setSuccess(false).setErrorMsg(errorMsg)
                .setRedirect(redirect).build();
        setValueResponse(response);
    }

    protected void success(final long value) {
        final ValueResponse response = ValueResponse.newBuilder().setValue(value).setSuccess(true).build();
        setValueResponse(response);
    }
}
