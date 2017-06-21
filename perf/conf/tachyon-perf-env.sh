#!/usr/bin/env bash

# The following gives an example:

# The workspace dir in Tachyon
export TACHYON_PERF_WORKSPACE="/tmp/tachyon-perf-workspace"

# The report output path
export TACHYON_PERF_OUT_DIR="$TACHYON_PERF_HOME/result"

# The tachyon-perf master service address
TACHYON_PERF_MASTER_HOSTNAME="slave001"
TACHYON_PERF_MASTER_PORT=23333

# The number of threads per worker
TACHYON_PERF_THREADS_NUM=1

# The slave is considered to be failed if not register in this time
TACHYON_PERF_UNREGISTER_TIMEOUT_MS=10000

# If true, the TachyonPerfSupervision will print the names of those running and remaining nodes
TACHYON_PERF_STATUS_DEBUG="true"

# If true, the test will abort when the number of failed nodes more than the threshold
TACHYON_PERF_FAILED_ABORT="true"
TACHYON_PERF_FAILED_PERCENTAGE=1

# If true the perf tool is installed on a shared file system visible to all slaves so copying
# and collating configurations either is a no-op or a local copy rather than a scp
TACHYON_PERF_SHARED_FS="false"

PERF_CONF_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Specify this value and add to the java opts to test against another fs client impl.
# 'HDFS' will use HDFS's native FileSystem client
# 'THCI' will use Tachyon's implementation (AbstractTFS) of HDFS's FileSystem interface
# Any other value, including empty, will default to Tachyon's native FileSystem client.
#TACHYON_PERF_UFS="Tachyon"
#export TACHYON_PERF_JAVA_OPTS+="-Dtachyon.perf.ufs=$TACHYON_PERF_UFS"

export TACHYON_PERF_JAVA_OPTS+="
  -Dlog4j.configuration=file:$PERF_CONF_DIR/log4j.properties
  -Dtachyon.perf.failed.abort=$TACHYON_PERF_FAILED_ABORT
  -Dtachyon.perf.failed.percentage=$TACHYON_PERF_FAILED_PERCENTAGE
  -Dtachyon.perf.status.debug=$TACHYON_PERF_STATUS_DEBUG
  -Dtachyon.perf.master.hostname=$TACHYON_PERF_MASTER_HOSTNAME
  -Dtachyon.perf.master.port=$TACHYON_PERF_MASTER_PORT
  -Dtachyon.perf.work.dir=$TACHYON_PERF_WORKSPACE
  -Dtachyon.perf.out.dir=$TACHYON_PERF_OUT_DIR
  -Dtachyon.perf.threads.num=$TACHYON_PERF_THREADS_NUM
  -Dtachyon.perf.unregister.timeout.ms=$TACHYON_PERF_UNREGISTER_TIMEOUT_MS
"
