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

package org.sonatype.nexus.ux.repository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.GroupRepository;
import org.sonatype.nexus.proxy.repository.HostedRepository;
import org.sonatype.nexus.proxy.repository.ProxyRepository;
import org.sonatype.nexus.proxy.repository.RemoteStatus;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.repository.ShadowRepository;
import org.sonatype.nexus.rapture.direct.DirectResource;
import org.sonatype.nexus.rapture.direct.Response;
import org.sonatype.nexus.rest.RepositoryURLBuilder;
import org.sonatype.nexus.ux.model.RepositoryInfoUX;

import com.director.core.annotation.DirectMethod;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import static org.sonatype.nexus.rapture.direct.Responses.error;
import static org.sonatype.nexus.rapture.direct.Responses.success;

/**
 * Repository Ext.Direct resource.
 *
 * @since 2.7
 */
@Named
@Singleton
public class RepositoryDirectResource
    implements DirectResource
{

  private final RepositoryRegistry repositoryRegistry;

  private final RepositoryURLBuilder repositoryURLBuilder;

  @Inject
  public RepositoryDirectResource(final RepositoryRegistry repositoryRegistry,
                                  final RepositoryURLBuilder repositoryURLBuilder)
  {
    this.repositoryRegistry = repositoryRegistry;
    this.repositoryURLBuilder = repositoryURLBuilder;
  }

  /**
   * Retrieve a list of available repositories info.
   */
  @DirectMethod
  public Response readInfo() {
    try {
      return success(Lists.transform(repositoryRegistry.getRepositories(), new Function<Repository, RepositoryInfoUX>()
      {
        @Override
        public RepositoryInfoUX apply(final Repository input) {
          RepositoryInfoUX info = new RepositoryInfoUX()
              .withId(input.getId())
              .withName(input.getName())
              .withType(getRepositoryType(input))
              .withFormat(input.getProviderHint())
              .withLocalStatus(input.getLocalStatus().toString())
              .withUrl(repositoryURLBuilder.getExposedRepositoryContentUrl(input));

          ProxyRepository proxyRepository = input.adaptToFacet(ProxyRepository.class);
          if (proxyRepository != null) {
            RemoteStatus remoteStatus = proxyRepository.getRemoteStatus(
                new ResourceStoreRequest(RepositoryItemUid.PATH_ROOT), false
            );
            info
                .withProxyMode(proxyRepository.getProxyMode().toString())
                .withRemoteStatus(remoteStatus.toString())
                .withRemoteStatusReason(remoteStatus.getReason());
          }

          return info;
        }
      }));
    }
    catch (Exception e) {
      return error(e);
    }
  }

  @DirectMethod
  public Response delete(final String id) {
    try {
      repositoryRegistry.removeRepository(id);
      return success().shouldRefresh();
    }
    catch (NoSuchRepositoryException e) {
      return error(e).shouldRefresh();
    }
    catch (Exception e) {
      return error(e);
    }
  }

  private String getRepositoryType(final Repository repository) {
    if (repository.getRepositoryKind().isFacetAvailable(ProxyRepository.class)) {
      return "Proxy";
    }
    else if (repository.getRepositoryKind().isFacetAvailable(HostedRepository.class)) {
      return "Hosted";
    }
    else if (repository.getRepositoryKind().isFacetAvailable(ShadowRepository.class)) {
      return "Virtual";
    }
    else if (repository.getRepositoryKind().isFacetAvailable(GroupRepository.class)) {
      return "Group";
    }
    return null;
  }

}
