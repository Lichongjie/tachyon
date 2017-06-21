/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.perf.benchmark.pattern.lfu;

import tachyon.perf.basic.PerfThread;
import tachyon.perf.benchmark.SimpleTaskContext;
import tachyon.perf.benchmark.pattern.lru.LRUThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shupeng on 2016/1/19.
 */
public class LFUTaskContext extends SimpleTaskContext {
    @Override
    public void setFromThread(PerfThread[] threads) {
        mAdditiveStatistics = new HashMap<String, List<Double>>(6);
        List<Double> basicWriteTime = new ArrayList<Double>(threads.length);
        List<Double> readTime = new ArrayList<Double>(threads.length);
        List<Double> tmpWriteTime = new ArrayList<Double>(threads.length);
        List<Double> basicThroughputs = new ArrayList<Double>(threads.length);
        List<Double> readThroughputs = new ArrayList<Double>(threads.length);
        List<Double> tmpWriteThroughput = new ArrayList<Double>(threads.length);
        for (PerfThread thread : threads) {
            if (!((LFUThread) thread).getSuccess()) {
                mSuccess = false;
            }
            basicThroughputs.add(((LFUThread) thread).getBasicWriteThroughput());
            readThroughputs.add(((LFUThread) thread).getReadThroughput());
            tmpWriteThroughput.add(((LFUThread) thread).getTmpWriteThroughput());
            basicWriteTime.add(((LFUThread) thread).getBasicWriteTime());
            readTime.add(((LFUThread) thread).getReadTime());
            tmpWriteTime.add(((LFUThread) thread).getTmpWriteTime());
        }
        mAdditiveStatistics.put("BasicWriteThroughput(MB/s)", basicThroughputs);
        mAdditiveStatistics.put("ReadThroughput(MB/s)", readThroughputs);
        mAdditiveStatistics.put("TmpWriteThroughput(MB/s)", tmpWriteThroughput);
        mAdditiveStatistics.put("BasicWriteTime(s)", basicWriteTime);
        mAdditiveStatistics.put("ReadTime(s)", readTime);
        mAdditiveStatistics.put("TmpWriteTime(s)", tmpWriteTime);
    }
}
