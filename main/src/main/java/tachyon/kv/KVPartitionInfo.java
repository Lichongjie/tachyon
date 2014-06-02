package tachyon.kv;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import tachyon.thrift.ClientStorePartitionInfo;
import tachyon.thrift.NetAddress;
import tachyon.util.CommonUtils;

/**
 * This is one type of partition.
 */
public class KVPartitionInfo {
  public final int STORE_ID;
  public final int PARTITION_INDEX;
  public final int DATA_FILE_ID;
  public final int INDEX_FILE_ID;

  public final ByteBuffer START_KEY;
  public final ByteBuffer END_KEY;

  private Set<NetAddress> mLocations = new HashSet<NetAddress>();

  KVPartitionInfo(int storeId, int partitionIndex, int dataFileId, int indexFileId,
      ByteBuffer start, ByteBuffer end) {
    STORE_ID = storeId;
    PARTITION_INDEX = partitionIndex;
    DATA_FILE_ID = dataFileId;
    INDEX_FILE_ID = indexFileId;

    START_KEY = CommonUtils.cloneByteBuffer(start);
    END_KEY = CommonUtils.cloneByteBuffer(end);
  }

  public synchronized void addLocation(NetAddress address) {
    mLocations.add(address);
  }

  public synchronized boolean containsLocation(NetAddress address) {
    return mLocations.contains(address);
  }

  public synchronized ClientStorePartitionInfo generateClientStorePartitionInfo() {
    ClientStorePartitionInfo res = new ClientStorePartitionInfo();
    res.setStoreId(STORE_ID);
    res.setPartitionIndex(PARTITION_INDEX);
    res.setDataFileId(DATA_FILE_ID);
    res.setIndexFileId(INDEX_FILE_ID);
    res.setStartKey(START_KEY.array());
    res.setEndKey(END_KEY.array());
    if (mLocations.size() > 0) {
      res.setLocation(mLocations.iterator().next());
    }
    return res;
  }

  public synchronized boolean hasLocation() {
    return !mLocations.isEmpty();
  }

  public synchronized void removeLocation(NetAddress address) {
    mLocations.remove(address);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("KVPartitionInfo(");
    sb.append("STORE_ID ").append(STORE_ID);
    sb.append(", PARTITION_INDEX ").append(PARTITION_INDEX);
    sb.append(", DATA_FILE_ID ").append(DATA_FILE_ID);
    sb.append(", INDEX_FILE_ID ").append(INDEX_FILE_ID);
    sb.append(", START_KEY ").append(START_KEY);
    sb.append(", END_KEY ").append(END_KEY);
    sb.append(")");
    return sb.toString();
  }
}
