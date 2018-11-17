package alluxio.client.file.cache;

import alluxio.client.file.cache.submodularLib.ISK;
import alluxio.client.file.cache.submodularLib.IterateOptimizer;
import alluxio.client.file.cache.submodularLib.cacheSet.CacheSet;
import alluxio.client.file.cache.submodularLib.cacheSet.CacheSetUtils;
import alluxio.client.file.cache.submodularLib.cacheSet.GR;
import alluxio.exception.AlluxioException;

import javax.swing.event.CaretListener;
import java.io.IOException;
import java.util.*;

import static alluxio.client.file.cache.ClientCacheContext.mPromotionThreadId;

public class PromotionPolicy implements Runnable {
  private long mCacheCapacity;
  private IterateOptimizer<CacheUnit> mOptimizer;
  public CacheSet mInputSpace1;
  public CacheSet mInputSpace2;
  public volatile boolean useOne = true;
  public boolean isProtomoting = false;
  private final Object mAccessLock = new Object();
  private final ClientCacheContext mContext = ClientCacheContext.INSTANCE;
  private volatile int mSize;
  private volatile int mNewSize;

  public void setPolicy(CachePolicy.PolicyName name) {
    if (name == CachePolicy.PolicyName.ISK) {
      mOptimizer = null;
      mOptimizer = new ISK((mCacheCapacity), new CacheSetUtils());
    } else if (name == CachePolicy.PolicyName.GR) {
      mOptimizer = null;
      mOptimizer = new GR((mCacheCapacity), new CacheSetUtils());
    }
  }

  public CacheSet move(CacheSet s1, CacheSet s2) {
    for (long fileId : s2.cacheMap.keySet()) {
      Set<CacheUnit> s = s2.get(fileId);
      if (!s1.cacheMap.containsKey(fileId)) {
        s1.cacheMap.put(fileId, new TreeSet<>(new Comparator<CacheUnit>() {
          @Override
          public int compare(CacheUnit o1, CacheUnit o2) {
            return 0;
          }
        }));
      }
      s1.get(fileId).addAll(s);
    }
    s2.clear();
    return s1;
  }


  public void filter(BaseCacheUnit unit1) {
    synchronized (mAccessLock) {
      if (useOne) {
        if (mSize > 2500) {
          mInputSpace1.clear();
        }
        mInputSpace1.add(unit1);
        mInputSpace1.addSort(unit1);
      } else {
        if (mSize > 2000) {
          mInputSpace2.clear();
        }
        mInputSpace2.add(unit1);
        mInputSpace2.addSort(unit1);
      }
      mSize++;
    }

  }

  public void update() {
    System.out.println("start update");
    isProtomoting = true;
    synchronized (mAccessLock) {
      if (useOne) {
        mOptimizer.addInputSpace(mInputSpace1);
        System.out.println(mInputSpace1.size());
      } else {
        mOptimizer.addInputSpace(mInputSpace2);
        System.out.println(mInputSpace2.size());

      }
      useOne = !useOne;
    }
    mOptimizer.optimize();
    CacheSet result = (CacheSet) mOptimizer.getResult();
    mOptimizer.clear();
    synchronized (mAccessLock) {
      promoteReset();
    }
    try {
      result.convertSort();
      //System.out.println("result: " + result.size());
      mContext.merge(result);
    } catch (IOException | AlluxioException e) {
      throw new RuntimeException(e);
    }
    System.out.println("update finish");

  }

  private void promoteReset() {
    mSize = 0;
  }

  private boolean promoteCheck() {
    return mSize > 1000;
  }

  public void init(long limit) {
    mCacheCapacity = limit;
    mInputSpace1 = new CacheSet();
    mInputSpace2 = new CacheSet();
    setPolicy(CachePolicy.PolicyName.ISK);
    mContext.stopCache();
    mContext.COMPUTE_POOL.submit(this);
  }


  @Override
  public void run() {
    mPromotionThreadId = Thread.currentThread().getId();
    System.out.println("promoter begins to run");
    while (true) {
      try {
        if (promoteCheck()) {
          update();
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
