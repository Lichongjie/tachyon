package tachyon.kv;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import tachyon.Constants;
import tachyon.util.CommonUtils;

/**
 * Metadata of a key/value store in the master.
 */
public class KVStoreInfo {
  private final Logger LOG = Logger.getLogger(Constants.LOGGER_TYPE);

  public final int INODE_ID;

  private List<KVPartitionInfo> mPartitions = new ArrayList<KVPartitionInfo>();

  public KVStoreInfo(int inodeId) {
    INODE_ID = inodeId;
  }

  void addPartition(KVPartitionInfo partition) throws IOException {
    // TODO this method is very inefficient currently.

    while (mPartitions.size() <= partition.PARTITION_INDEX) {
      mPartitions.add(null);
    }

    if (mPartitions.get(partition.PARTITION_INDEX) != null) {
      throw new IOException("Partition has been added before: " + partition);
    }

    mPartitions.set(partition.PARTITION_INDEX, partition);

    for (int i = 0; i < mPartitions.size(); i ++) {
      if (mPartitions.get(i) == null) {
        continue;
      }
      for (int j = i + 1; j < mPartitions.size(); j ++) {
        if (mPartitions.get(j) == null) {
          continue;
        }
        int result = mPartitions.get(i).END_KEY.compareTo(mPartitions.get(j).START_KEY);
        if (result > 0) {
          throw new IOException("Wrong partition order: " + mPartitions.get(i) + " > "
              + mPartitions.get(j));
        }
        break;
      }
    }
  }

  KVPartitionInfo getPartition(ByteBuffer buf) {
    // TODO Make this method efficient.
    for (int k = 0; k < mPartitions.size(); k ++) {
      KVPartitionInfo partition = mPartitions.get(k);
      if (null == partition) {
        LOG.warn("KVStore " + INODE_ID + " has null partition when being queried.");
        continue;
      }
      LOG.info("GetPartition: " + partition + " " + buf);
      LOG.info("GetPartition 2 : " + partition.START_KEY.array() + " " + buf.array() + " "
          + partition.END_KEY.array());
      if (CommonUtils.compare(partition.START_KEY, buf) <= 0
          && CommonUtils.compare(partition.END_KEY, buf) >= 0) {
        return partition;
      }
    }
    return null;
  }

  KVPartitionInfo getPartition(int partitionIndex) {
    return mPartitions.get(partitionIndex);
  }
}
