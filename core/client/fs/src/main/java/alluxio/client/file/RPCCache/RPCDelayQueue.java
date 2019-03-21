package alluxio.client.file.RPCCache;

import alluxio.client.file.FileSystemContext;
import alluxio.collections.Pair;
import alluxio.network.netty.NettyRPC;
import alluxio.network.netty.NettyRPCContext;
import alluxio.proto.dataserver.Protocol;
import alluxio.util.proto.ProtoMessage;
import alluxio.wire.WorkerNetAddress;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum RPCDelayQueue {
  INSTANCE;
  private Map<Long, Protocol.LocalBlockCloseRequest> msgCache = new ConcurrentHashMap<>();
  private Map<Long, Long> mPutTime = new ConcurrentHashMap<>();
  private Map<Long, Pair<WorkerNetAddress, Channel>> tmp = new HashMap<>();

  public Map<Long, ProtoMessage> tmp2 = new HashMap<>();
  private long delayTime = 5000;


  public synchronized void add(Protocol.LocalBlockCloseRequest request, WorkerNetAddress address, Channel channel) {
    long blockId = request.getBlockId();
    if (msgCache.containsKey(blockId)) {
      mPutTime.put(blockId, System.currentTimeMillis());
    } else {
      msgCache.put(blockId, request);
      mPutTime.put(blockId, System.currentTimeMillis());
      if (tmp.containsKey(blockId)) {
        tmp.put(blockId, new Pair<>(address, channel));
      }
    }
  }

  public synchronized boolean hit(Protocol.LocalBlockOpenRequest request) {
    if (msgCache.containsKey(request.getBlockId())) {
      msgCache.remove(request.getBlockId());
      mPutTime.remove(request.getBlockId());
      return true;
    }
    return false;
  }


  class msgOutOfTimeChecker implements Runnable {
    @Override
    public void run() {
      long currTime = System.currentTimeMillis();
      Set<Long> needRemove = new HashSet<>();
      for (long e : mPutTime.keySet()) {
        long time = mPutTime.get(e);
        if (currTime - time >delayTime ) {
            needRemove.add(e);
        }
      }
      for (long blockId : needRemove) {
        mPutTime.remove(blockId);
      }
      for (long blockId: needRemove) {
        if (!mPutTime.containsKey(blockId)) {
          try {
            NettyRPC.call(NettyRPCContext.defaults().setChannel(tmp.get(blockId).getSecond()).setTimeout(30 * 1000),
                    new ProtoMessage(msgCache.get(blockId)));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }  finally
          {
            FileSystemContext.get().releaseNettyChannel(tmp.get(blockId).getFirst(), tmp.get(blockId).getSecond());
            msgCache.remove(blockId);
          }
        }
      }
    }
  }
}
