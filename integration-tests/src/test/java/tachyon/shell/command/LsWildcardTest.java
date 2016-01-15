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
import org.powermock.reflect.Whitebox;

import tachyon.Constants;
import tachyon.TachyonURI;
import tachyon.exception.TachyonException;
import tachyon.security.LoginUser;
import tachyon.shell.AbstractTfsShellTest;
import tachyon.shell.TfsShellUtilsTest;
import tachyon.util.FormatUtils;

/**
 * Test for ls with a wildcard argument
 */
public class LsWildcardTest extends AbstractTfsShellTest {
  @Test
  public void lsWildcardTest() throws IOException, TachyonException {
    // clear the loginUser
    Whitebox.setInternalState(LoginUser.class, "sLoginUser", (String) null);

    String testUser = "test_user_lsWildcard";
    System.setProperty(Constants.SECURITY_LOGIN_USERNAME, testUser);

    TfsShellUtilsTest.resetTachyonFileHierarchy(mTfs);

    String expect = "";
    expect += getLsResultStr(new TachyonURI("/testWildCards/bar/foobar3"), 30, testUser);
    expect += getLsResultStr(new TachyonURI("/testWildCards/foo/foobar1"), 10, testUser);
    expect += getLsResultStr(new TachyonURI("/testWildCards/foo/foobar2"), 20, testUser);
    mFsShell.run("ls", "/testWildCards/*/foo*");
    Assert.assertEquals(expect, mOutput.toString());

    expect += getLsResultStr(new TachyonURI("/testWildCards/bar/foobar3"), 30, testUser);
    expect += getLsResultStr(new TachyonURI("/testWildCards/foo/foobar1"), 10, testUser);
    expect += getLsResultStr(new TachyonURI("/testWildCards/foo/foobar2"), 20, testUser);
    expect += getLsResultStr(new TachyonURI("/testWildCards/foobar4"), 40, testUser);
    mFsShell.run("ls", "/testWildCards/*");
    Assert.assertEquals(expect, mOutput.toString());
    // clear testing username
    System.clearProperty(Constants.SECURITY_LOGIN_USERNAME);
  }

  private String getLsResultStr(TachyonURI tUri, int size, String testUser) throws IOException,
      TachyonException {
    String format = "%-10s%-25s%-15s%-15s%-5s\n";
    return String.format(format, FormatUtils.getSizeFromBytes(size),
        CommandUtils.convertMsToDate(mTfs.getInfo(mTfs.open(tUri)).getCreationTimeMs()),
        "In Memory", testUser, tUri.getPath());
  }
}
