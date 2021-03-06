
package org.xbib.elasticsearch.action.admin.cluster.state;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.support.master.TransportMasterNodeOperationAction;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.NodeEnvironment;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;
import org.xbib.elasticsearch.skywalker.Skywalker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.cluster.ClusterState.newClusterStateBuilder;

/**
 *  Transport consistency check action
 *
 */
public class TransportConsistencyCheckAction extends TransportMasterNodeOperationAction<ConsistencyCheckRequest, ConsistencyCheckResponse> {

    private final ClusterName clusterName;

    private final NodeEnvironment nodeEnv;

    @Inject
    public TransportConsistencyCheckAction(Settings settings, TransportService transportService, ClusterService clusterService, ThreadPool threadPool,
                                           ClusterName clusterName, NodeEnvironment nodeEnvironment) {
        super(settings, transportService, clusterService, threadPool);
        this.clusterName = clusterName;
        this.nodeEnv = nodeEnvironment;
    }

    @Override
    protected String executor() {
        return ThreadPool.Names.GENERIC;
    }

    @Override
    protected String transportAction() {
        return ConsistencyCheckAction.NAME;
    }

    @Override
    protected ConsistencyCheckRequest newRequest() {
        return new ConsistencyCheckRequest();
    }

    @Override
    protected ConsistencyCheckResponse newResponse() {
        return new ConsistencyCheckResponse();
    }

    @Override
    protected boolean localExecute(ConsistencyCheckRequest request) {
        return true;
    }

    @Override
    protected ConsistencyCheckResponse masterOperation(ConsistencyCheckRequest request, ClusterState state) throws ElasticSearchException {
        ClusterState.Builder builder = newClusterStateBuilder();
        List<File> files = new ArrayList();
        builder.metaData(Skywalker.loadState(files, nodeEnv));
        return new ConsistencyCheckResponse(clusterName, builder.build(), files);
    }

}
