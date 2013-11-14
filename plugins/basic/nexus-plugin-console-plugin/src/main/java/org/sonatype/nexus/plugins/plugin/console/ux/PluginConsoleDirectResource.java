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

package org.sonatype.nexus.plugins.plugin.console.ux;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.plugins.plugin.console.PluginConsoleManager;
import org.sonatype.nexus.rapture.direct.DirectResource;
import org.sonatype.nexus.rapture.direct.Response;
import org.sonatype.sisu.goodies.common.ComponentSupport;

import com.director.core.annotation.DirectMethod;

import static org.sonatype.nexus.rapture.direct.Responses.error;
import static org.sonatype.nexus.rapture.direct.Responses.success;

/**
 * Plugin Console Ext.Direct resource.
 *
 * @since 2.7
 */
@Named
@Singleton
public class PluginConsoleDirectResource
    extends ComponentSupport
    implements DirectResource
{

  private final PluginConsoleManager pluginConsoleManager;

  @Inject
  public PluginConsoleDirectResource(final PluginConsoleManager pluginConsoleManager) {
    this.pluginConsoleManager = pluginConsoleManager;
  }

  @DirectMethod
  public Response read() {
    try {
      return success(pluginConsoleManager.listPluginInfo());
    }
    catch (Exception e) {
      return error(e);
    }
  }

}
