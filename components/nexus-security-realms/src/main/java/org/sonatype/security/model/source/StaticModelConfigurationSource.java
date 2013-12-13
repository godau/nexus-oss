/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2013 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.security.model.source;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.inject.Typed;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.configuration.ConfigurationException;
import org.sonatype.security.model.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A special "static" configuration source, that always return a factory provided defaults for Security configuration.
 * It is unmodifiable, since it actually reads the bundled config file from the module's JAR.
 *
 * @author cstamas
 */
@Singleton
@Typed(SecurityModelConfigurationSource.class)
@Named("static")
public class StaticModelConfigurationSource
    extends AbstractSecurityModelConfigurationSource
{
  private static final Logger log = LoggerFactory.getLogger(StaticModelConfigurationSource.class);

  private static final String STATIC_SECURITY_RESOURCE = "/META-INF/security/security.xml";

  /**
   * Gets the configuration using getResourceAsStream from "/META-INF/security/security.xml".
   */
  public InputStream getConfigurationAsStream()
      throws IOException
  {
    return getClass().getResourceAsStream(STATIC_SECURITY_RESOURCE);
  }

  public Configuration loadConfiguration()
      throws ConfigurationException, IOException
  {
    if (getClass().getResource(STATIC_SECURITY_RESOURCE) != null) {
      loadConfiguration(getConfigurationAsStream());
    }
    else {
      this.log.warn("Default static security configuration not found in classpath: "
          + STATIC_SECURITY_RESOURCE);
    }

    Configuration configuration = getConfiguration();

    return configuration;
  }

  /**
   * This method will always throw UnsupportedOperationException, since SecurityDefaultsConfigurationSource is read
   * only.
   */
  public void storeConfiguration()
      throws IOException
  {
    throw new UnsupportedOperationException("The SecurityDefaultsConfigurationSource is static source!");
  }

  /**
   * Static configuration has no default source, hence it cannot be defalted. Always returns false.
   */
  public boolean isConfigurationDefaulted() {
    return false;
  }

  @Override
  public void backupConfiguration()
      throws IOException
  {
    throw new UnsupportedOperationException("The SecurityDefaultsConfigurationSource is a read only source!");
  }

}
