package org.gw.raft;

import com.alipay.remoting.exception.CodecException;
import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.error.RaftException;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotReader;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotWriter;
import com.alipay.sofa.jraft.util.Utils;
import org.gw.raft.remote.serialize.SerializerManager;
import org.gw.raft.snapshot.CounterSnapshotFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author guanwu
 * @created on 2022-07-12 16:08:31
 **/
public class CounterServerStateMachine extends StateMachineAdapter {

    private static final Logger LOG        = LoggerFactory.getLogger(CounterServerStateMachine.class);

    private AtomicLong value = new AtomicLong(0);

    private final AtomicLong    leaderTerm = new AtomicLong(-1);

    public boolean isLeader() {
        return this.leaderTerm.get() > 0;
    }


    /**
     * Returns current value.
     */
    public long getValue() {
        return this.value.get();
    }
    @Override
    public void onApply(Iterator iterator) {
        while (iterator.hasNext()) {
            long current = 0;
            CounterOperation counterOperation = null;
            CounterClosure closure = null;
            if (iterator.done() != null) {
                //leader端逻辑
                closure = (CounterClosure) iterator.done();
                counterOperation = closure.getCounterOperation();
            } else {
                //follower需要执行反序列化请求
                ByteBuffer data = iterator.getData();
                try {
                    counterOperation = SerializerManager.getSerializer(SerializerManager.Hessian2)
                            .deserialize(data.array(),CounterOperation.class.getName());
                } catch (CodecException e) {
                    LOG.error("Failed to decode incrmentAndGet ");
                }
            }
            if (counterOperation != null) {
                switch (counterOperation.getOp()) {
                    case CounterOperation.GET:
                        current = this.value.get();
                        LOG.info("get value:{}, index:{}", current, iterator.getIndex());
                        break;
                    case CounterOperation.INCREMENT:
                        final long delta = counterOperation.getDelta();
                        final long prev = this.value.get();
                        current = this.value.getAndAdd(delta);
                        LOG.info("Added value={} by delta={} at logIndex={}", prev, delta, iterator.getIndex());
                        break;
                    default:
                        break;
                }

                if (null != closure) {
                    closure.success(current);
                    closure.run(Status.OK());
                }
            }
            iterator.next();
        }
    }

    @Override
    public void onSnapshotSave(SnapshotWriter writer, Closure done) {
        final long currVal = this.value.get();
        Utils.runInThread(() -> {
            final CounterSnapshotFile snapshot = new CounterSnapshotFile(writer.getPath() + File.separator + "data");
            if (snapshot.save(currVal)) {
                if (writer.addFile("data")) {
                    done.run(Status.OK());
                } else {
                    done.run(new Status(RaftError.EIO, "Fail to add file to writer"));
                }
            } else {
                done.run(new Status(RaftError.EIO, "Fail to save counter snapshot %s", snapshot.getPath()));
            }
        });
    }

    @Override
    public boolean onSnapshotLoad(SnapshotReader reader) {
        if (isLeader()) {
            LOG.warn("Leader is not supposed to load snapshot");
            return false;
        }
        final CounterSnapshotFile snapshot = new CounterSnapshotFile(reader.getPath() + File.separator + "data");
        try {
            this.value.set(snapshot.load());
            return true;
        } catch (final IOException e) {
            LOG.error("Fail to load snapshot from {}", snapshot.getPath());
            return false;
        }
    }

    @Override
    public void onLeaderStop(Status status) {
        this.leaderTerm.set(-1);
        super.onLeaderStop(status);
    }

    @Override
    public void onLeaderStart(long term) {
        this.leaderTerm.set(term);
        super.onLeaderStart(term);
    }

    @Override
    public void onError(RaftException e) {
        LOG.error("Raft error: {}", e, e);
    }
}
