package alluxio.client.file.cache.test.MTTest;

import alluxio.client.file.cache.CacheUnit;
import alluxio.client.file.cache.ClientCacheContext;
import alluxio.client.file.cache.TempCacheUnit;
import alluxio.client.file.cache.UnlockTask;
import alluxio.client.file.cache.test.LRUEvictor;

import java.util.List;

public abstract class BaseEvictContext {
  public double mHitRatio;
  public double mHitSize;
  public double mVisitSize;
  protected long mCacheSize = 0;
  public long mTestFileId;
  public long mTestFileLength;
  public long mCacheCapacity;
  public UnlockTask unlockTask = new UnlockTask();
  public ClientCacheContext mCacheContext;


  public double computeHitRatio() {
    mHitRatio = mHitSize / mVisitSize;
    return mHitRatio;
  }


  public BaseEvictContext(LRUEvictor test, ClientCacheContext cacheContext) {
    mTestFileId = test.mTestFileId;
    mTestFileLength = test.mTestFileLength;
    mCacheCapacity = test.cacheSize;
    mCacheContext = cacheContext;
  }

  public BaseEvictContext resetCapacity(long capacitySize) {
    mCacheCapacity = capacitySize;
    return this;
  }

  public long accessByShare(TmpCacheUnit unit, ClientCacheContext sharedContext) {
    CacheUnit unit1 = sharedContext.getCache(mTestFileId, mTestFileLength, unit.getBegin(), unit.getEnd(), unlockTask);
    if (unit1.isFinish()) {
      access(unit);
    }
    return access(unit);

  }

  public abstract List<TmpCacheUnit> getCacheList();

  public abstract long access(TmpCacheUnit unit);

  public abstract long remove(TmpCacheUnit unit);

  public abstract TmpCacheUnit getEvictUnit();

  public abstract TmpCacheUnit getMaxPriorityUnit();

  public abstract void evict();
}
