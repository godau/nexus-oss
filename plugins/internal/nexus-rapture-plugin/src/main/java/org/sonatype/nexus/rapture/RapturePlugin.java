package org.sonatype.nexus.rapture;

import javax.inject.Named;

import org.eclipse.sisu.EagerSingleton;
import org.sonatype.nexus.plugin.PluginIdentity;

/**
 * Rapture plugin.
 *
 * @since 2.7
 */
@Named
@EagerSingleton
public class RapturePlugin
    extends PluginIdentity
{
  /**
   * Prefix for ID-like things.
   */
  public static final String ID_PREFIX = "rapture";

  /**
   * Expected groupId for plugin artifact.
   */
  public static final String GROUP_ID = "org.sonatype.nexus.plugins";

  /**
   * Expected artifactId for plugin artifact.
   */
  public static final String ARTIFACT_ID = "nexus-" + ID_PREFIX + "-plugin";

  public RapturePlugin() throws Exception {
    super(GROUP_ID, ARTIFACT_ID);
  }
}
