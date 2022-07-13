package org.gw.raft;

import java.io.Serializable;

/**
 * @author guanwu
 * @created on 2022-07-12 17:15:29
 **/
public class CounterOperation implements Serializable {

    private static final long serialVersionUID = 3255316885906196510L;

    public static final byte GET = 0x01;
    public static final byte INCREMENT = 0x02;
    private byte op;
    private long delta;

    public CounterOperation(byte get) {
        this.op = get;
    }

    public CounterOperation(byte inc, long delta) {
        this.op = inc;
        this.delta = delta;
    }

    public static CounterOperation createGet() {
        return new CounterOperation(GET);
    }

    public static CounterOperation createIncrement(final long delta) {
        return new CounterOperation(INCREMENT, delta);
    }

    public byte getOp() {
        return op;
    }

    public long getDelta() {
        return delta;
    }

}
