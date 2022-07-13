package org.gw.raft;

import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.RaftRpcServerFactory;
import com.alipay.sofa.jraft.rpc.RpcServer;
import org.apache.commons.io.FileUtils;
import org.gw.raft.rpc.CounterGrpcHelper;
import org.gw.raft.rpc.GetValueRequestProcessor;
import org.gw.raft.rpc.IncrementAndGetRequestProcessor;
import org.gw.raft.rpc.ValueResponse;

import java.io.File;
import java.io.IOException;

/**
 * @author guanwu
 * @created on 2022-07-13 14:34:28
 **/
public class CounterServer {
    private RaftGroupService raftGroupService;
    private Node node;
    private CounterServerStateMachine cfsm;

    public CounterServerStateMachine getFsm() {
        return cfsm;
    }

    public Node getNode() {
        return node;
    }

    public CounterServer(final String dataPath, final String groupId, final PeerId serverId,
                         final NodeOptions nodeOptions) throws IOException {
        FileUtils.forceMkdir(new File(dataPath));
        final RpcServer rpcServer = RaftRpcServerFactory.createRaftRpcServer(serverId.getEndpoint());
        CounterGrpcHelper.initGRpc();
        CounterGrpcHelper.setRpcServer(rpcServer);
        CounterService counterService = new CounterServiceImpl(this);
        rpcServer.registerProcessor(new GetValueRequestProcessor(counterService));
        rpcServer.registerProcessor(new IncrementAndGetRequestProcessor(counterService));
        this.cfsm = new CounterServerStateMachine();
        // set fsm to nodeOptions
        nodeOptions.setFsm(this.cfsm);
        // set storage path (log,meta,snapshot)
        // log, must
        nodeOptions.setLogUri(dataPath + File.separator + "log");
        // meta, must
        nodeOptions.setRaftMetaUri(dataPath + File.separator + "raft_meta");
        // snapshot, optional, generally recommended
        nodeOptions.setSnapshotUri(dataPath + File.separator + "snapshot");
        // init raft group service framework

        this.raftGroupService = new RaftGroupService(groupId, serverId, nodeOptions, rpcServer);
        this.node = this.raftGroupService.start();
    }

    public RaftGroupService RaftGroupService() {
        return this.raftGroupService;
    }


    /**
     * Redirect request to new leader
     */
    public ValueResponse redirect() {
        final ValueResponse.Builder builder = ValueResponse.newBuilder().setSuccess(false);
        if (this.node != null) {
            final PeerId leader = this.node.getLeaderId();
            if (leader != null) {
                builder.setRedirect(leader.toString());
            }
        }
        return builder.build();
    }

    public static void main(final String[] args) throws IOException {
        if (args.length != 4) {
            System.out
                    .println("Usage : java com.alipay.sofa.jraft.example.counter.CounterServer {dataPath} {groupId} {serverId} {initConf}");
            System.out
                    .println("Example: java com.alipay.sofa.jraft.example.counter.CounterServer ~/counter/server1 counter 127.0.0.1:8081 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083");
            System.exit(1);
        }

        final String dataPath = args[0];
        final String groupId = args[1];
        final String serverIdStr = args[2];
        final String initConfStr = args[3];
        NodeOptions nodeOptions = new NodeOptions();
        nodeOptions.setElectionTimeoutMs(1000);
        nodeOptions.setDisableCli(false);
        nodeOptions.setSnapshotIntervalSecs(30);
        final PeerId peerId = new PeerId();
        if (!peerId.parse(serverIdStr)) {
            throw new IllegalArgumentException("Failed to parse serverId:" + serverIdStr);
        }
        final Configuration initConf = new Configuration();
        if (!initConf.parse(initConfStr)) {
            throw new IllegalArgumentException("Failed to parse initConf: " + initConfStr);
        }
        nodeOptions.setInitialConf(initConf);
        final CounterServer counterServer = new CounterServer(dataPath, groupId, peerId, nodeOptions);
        System.out.println("Started counter server at port:"
                + counterServer.getNode().getNodeId().getPeerId().getPort());
        CounterGrpcHelper.blockUntilShutdown();
    }
}
