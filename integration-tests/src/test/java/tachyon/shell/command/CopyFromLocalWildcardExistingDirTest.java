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

package tachyon.shell.command;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import tachyon.TachyonURI;
import tachyon.exception.TachyonException;
import tachyon.shell.AbstractTfsShellTest;
import tachyon.shell.TfsShellUtilsTest;

/**
 * Test used to copy files from local with a wildcard existing directory
 */
public class CopyFromLocalWildcardExistingDirTest extends AbstractTfsShellTest {
  @Test
  public void copyFromLocalWildcardExistingDirTest() throws IOException, TachyonException {
    TfsShellUtilsTest.resetLocalFileHierarchy(mLocalTachyonCluster);
    mTfs.mkdir(new TachyonURI("/testDir"));
    int ret =
        mFsShell.run("copyFromLocal", mLocalTachyonCluster.getTachyonHome()
            + "/testWildCards/*/foo*", "/testDir");
    Assert.assertEquals(0, ret);
    Assert.assertTrue(fileExist(new TachyonURI("/testDir/foobar1")));
    Assert.assertTrue(fileExist(new TachyonURI("/testDir/foobar2")));
    Assert.assertTrue(fileExist(new TachyonURI("/testDir/foobar3")));
  }
}
