/**
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.security.model.upgrade;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.sonatype.configuration.upgrade.ConfigurationIsCorruptedException;
import org.sonatype.configuration.upgrade.UpgradeMessage;
import org.sonatype.security.model.CRole;
import org.sonatype.security.model.v2_0_4.io.xpp3.SecurityConfigurationXpp3Reader;
import org.sonatype.security.model.v2_0_5.upgrade.BasicVersionUpgrade;

@Singleton
@Typed( value = SecurityUpgrader.class )
@Named( value = "2.0.4" )
public class Upgrade204to205
    implements SecurityUpgrader
{

    @Inject
    private Logger logger;

    public Object loadConfiguration( File file )
        throws IOException, ConfigurationIsCorruptedException
    {
        FileReader fr = null;

        try
        {
            // reading without interpolation to preserve user settings as variables
            fr = new FileReader( file );

            SecurityConfigurationXpp3Reader reader = new SecurityConfigurationXpp3Reader();

            return reader.read( fr );
        }
        catch ( XmlPullParserException e )
        {
            throw new ConfigurationIsCorruptedException( file.getAbsolutePath(), e );
        }
        finally
        {
            if ( fr != null )
            {
                fr.close();
            }
        }
    }

    public void upgrade( UpgradeMessage message )
        throws ConfigurationIsCorruptedException
    {
        org.sonatype.security.model.v2_0_4.Configuration oldc =
            (org.sonatype.security.model.v2_0_4.Configuration) message.getConfiguration();

        org.sonatype.security.model.Configuration newc = new BasicVersionUpgrade().upgradeConfiguration( oldc );

        CRole admin = new CRole();
        admin.setDescription( "Deprecated admin role, use nexus-admin instead" );
        admin.setId( "admin" );
        admin.setName( "Admin" );
        admin.setReadOnly( false );
        admin.addRole( "nexus-admin" );
        newc.addRole( admin );
        CRole developer = new CRole();
        developer.setDescription( "Deprecated developer role, use nexus-developer instead" );
        developer.setId( "developer" );
        developer.setName( "Developer" );
        developer.setReadOnly( false );
        developer.addRole( "nexus-developer" );
        newc.addRole( developer );
        CRole deployer = new CRole();
        deployer.setDescription( "Deprecated deployer role, use nexus-deployer instead" );
        deployer.setId( "deployer" );
        deployer.setName( "Deployer" );
        deployer.setReadOnly( false );
        deployer.addRole( "nexus-deployer" );
        newc.addRole( deployer );

        newc.setVersion( org.sonatype.security.model.Configuration.MODEL_VERSION );
        message.setModelVersion( org.sonatype.security.model.Configuration.MODEL_VERSION );
        message.setConfiguration( newc );
    }

}
