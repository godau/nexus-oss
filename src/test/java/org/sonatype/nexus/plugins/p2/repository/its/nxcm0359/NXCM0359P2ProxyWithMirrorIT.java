/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.p2.repository.its.nxcm0359;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.sonatype.nexus.plugins.p2.repository.its.AbstractNexusProxyP2IntegrationIT;
import org.sonatype.nexus.test.utils.TestProperties;

public class NXCM0359P2ProxyWithMirrorIT
    extends AbstractNexusProxyP2IntegrationIT
{
    public NXCM0359P2ProxyWithMirrorIT()
    {
        super( "nxcm0359" );
    }

    @Override
    public void startProxy()
        throws Exception
    {
        final String proxyRepoBaseUrl = TestProperties.getString( "proxy.repo.base.url" );

        replaceInFile( "target/nexus/proxy-repo/nxcm0359/artifacts.xml", "${proxy-repo-base-url}", proxyRepoBaseUrl );
        replaceInFile( "target/nexus/proxy-repo/nxcm0359/mirrors.xml", "${proxy-repo-base-url}", proxyRepoBaseUrl );

        super.startProxy();
    }

    @Test
    public void test()
        throws Exception
    {
        final String nexusTestRepoUrl = getNexusTestRepoUrl();

        final File installDir = new File( "target/eclipse/nxcm0359" );

        installUsingP2( nexusTestRepoUrl, "com.sonatype.nexus.p2.its.feature.feature.group",
            installDir.getCanonicalPath() );

        final File feature = new File( installDir, "features/com.sonatype.nexus.p2.its.feature_1.0.0" );
        Assert.assertTrue( feature.exists() && feature.isDirectory() );

        final File bundle = new File( installDir, "plugins/com.sonatype.nexus.p2.its.bundle_1.0.0.jar" );
        Assert.assertTrue( bundle.canRead() );
    }
}
