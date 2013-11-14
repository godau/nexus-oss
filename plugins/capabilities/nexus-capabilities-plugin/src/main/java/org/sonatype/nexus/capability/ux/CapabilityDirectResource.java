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

package org.sonatype.nexus.capability.ux;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.configuration.validation.InvalidConfigurationException;
import org.sonatype.nexus.capability.ux.model.CapabilityStatusUX;
import org.sonatype.nexus.capability.ux.model.CapabilityUX;
import org.sonatype.nexus.capability.ux.model.PropertyUX;
import org.sonatype.nexus.capability.ux.model.TagUX;
import org.sonatype.nexus.plugins.capabilities.Capability;
import org.sonatype.nexus.plugins.capabilities.CapabilityDescriptor;
import org.sonatype.nexus.plugins.capabilities.CapabilityNotFoundException;
import org.sonatype.nexus.plugins.capabilities.CapabilityReference;
import org.sonatype.nexus.plugins.capabilities.CapabilityRegistry;
import org.sonatype.nexus.plugins.capabilities.Tag;
import org.sonatype.nexus.plugins.capabilities.Taggable;
import org.sonatype.nexus.plugins.capabilities.support.CapabilityReferenceFilterBuilder;
import org.sonatype.nexus.rapture.direct.DirectResource;
import org.sonatype.nexus.rapture.direct.Response;
import org.sonatype.sisu.goodies.common.ComponentSupport;

import com.director.core.annotation.DirectMethod;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.plugins.capabilities.CapabilityIdentity.capabilityIdentity;
import static org.sonatype.nexus.plugins.capabilities.CapabilityType.capabilityType;
import static org.sonatype.nexus.rapture.direct.Responses.error;
import static org.sonatype.nexus.rapture.direct.Responses.id;
import static org.sonatype.nexus.rapture.direct.Responses.invalid;
import static org.sonatype.nexus.rapture.direct.Responses.success;

/**
 * Capabilities Ext.Direct resource.
 *
 * @since 2.7
 */
@Named
@Singleton
public class CapabilityDirectResource
    extends ComponentSupport
    implements DirectResource
{

  private static final Logger log = LoggerFactory.getLogger(CapabilityDirectResource.class);

  private final CapabilityRegistry capabilityRegistry;

  @Inject
  public CapabilityDirectResource(final CapabilityRegistry capabilityRegistry) {
    this.capabilityRegistry = checkNotNull(capabilityRegistry);
  }

  /**
   * Returns status of all capabilities.
   */
  @DirectMethod
  public Response readStatus() {
    try {
      final Collection<? extends CapabilityReference> references = capabilityRegistry.get(
          CapabilityReferenceFilterBuilder.capabilities()
      );
      return success(
          Lists.transform(Lists.newArrayList(references), new Function<CapabilityReference, CapabilityStatusUX>()
          {
            @Nullable
            @Override
            public CapabilityStatusUX apply(@Nullable final CapabilityReference input) {
              if (input == null) {
                return null;
              }
              return asCapabilityStatus(input);
            }
          })
      ).shouldRefresh();
    }
    catch (Exception e) {
      return error(e);
    }
  }

  /**
   * Returns all capabilities.
   */
  @DirectMethod
  public Response read() {
    try {
      final Collection<? extends CapabilityReference> references = capabilityRegistry.get(
          CapabilityReferenceFilterBuilder.capabilities()
      );
      return success(
          Lists.transform(Lists.newArrayList(references), new Function<CapabilityReference, CapabilityUX>()
          {
            @Nullable
            @Override
            public CapabilityUX apply(@Nullable final CapabilityReference input) {
              if (input == null) {
                return null;
              }
              return asCapability(input);
            }
          })
      ).shouldRefresh();
    }
    catch (Exception e) {
      return error(e);
    }
  }

  /**
   * Add a new capability.
   */
  @DirectMethod
  public Response create(final CapabilityUX capability) {
    try {
      return id(
          capabilityRegistry.add(
              capabilityType(capability.getTypeId()),
              capability.isEnabled(),
              capability.getNotes(),
              asMap(capability.getProperties())
          ).context().id().toString()
      ).shouldRefresh();
    }
    catch (InvalidConfigurationException e) {
      return invalid(e);
    }
    catch (Exception e) {
      return error(e);
    }
  }

  /**
   * Update the configuration of an existing capability.
   */
  @DirectMethod
  public Response update(final CapabilityUX capability) {
    try {
      return id(
          capabilityRegistry.update(
              capabilityIdentity(capability.getId()),
              capability.isEnabled(),
              capability.getNotes(),
              asMap(capability.getProperties())
          ).context().id().toString()
      ).shouldRefresh();
    }
    catch (InvalidConfigurationException e) {
      return invalid(e);
    }
    catch (CapabilityNotFoundException e) {
      return error(e).shouldRefresh();
    }
    catch (Exception e) {
      return error(e);
    }
  }

  /**
   * Delete an existing capability.
   */
  @DirectMethod
  public Response delete(final String id) {
    try {
      capabilityRegistry.remove(capabilityIdentity(id));
      return success().shouldRefresh();
    }
    catch (CapabilityNotFoundException e) {
      return error(e).shouldRefresh();
    }
    catch (Exception e) {
      return error(e);
    }
  }

  /**
   * Enable an existing capability.
   */
  @DirectMethod
  public Response enable(final String id) {
    try {
      capabilityRegistry.enable(capabilityIdentity(id));
      return success().shouldRefresh();
    }
    catch (CapabilityNotFoundException e) {
      return error(e).shouldRefresh();
    }
    catch (Exception e) {
      return error(e);
    }
  }

  /**
   * Disable an existing capability.
   */
  @DirectMethod
  public Response disable(final String id) {
    try {
      capabilityRegistry.disable(capabilityIdentity(id));
      return success().shouldRefresh();
    }
    catch (CapabilityNotFoundException e) {
      return error(e).shouldRefresh();
    }
    catch (Exception e) {
      return error(e);
    }
  }

  private static CapabilityStatusUX asCapabilityStatus(final CapabilityReference reference) {
    checkNotNull(reference);

    CapabilityDescriptor descriptor = reference.context().descriptor();
    Capability capability = reference.capability();

    final CapabilityStatusUX capabilityStatus = new CapabilityStatusUX()
        .withId(reference.context().id().toString())
        .withNotes(reference.context().notes())
        .withTypeId(descriptor.type().toString())
        .withTypeName(descriptor.name())
        .withEnabled(reference.context().isEnabled())
        .withActive(reference.context().isActive())
        .withError(reference.context().hasFailure());

    try {
      capabilityStatus.setDescription(capability.description());
    }
    catch (Exception ignore) {
      capabilityStatus.setDescription(null);
    }

    try {
      capabilityStatus.setStatus(capability.status());
    }
    catch (Exception ignore) {
      capabilityStatus.setStatus(null);
    }
    capabilityStatus.setStateDescription(reference.context().stateDescription());

    Set<Tag> tags = Sets.newHashSet();

    try {
      if (descriptor instanceof Taggable) {
        Set<Tag> tagSet = ((Taggable) descriptor).getTags();
        if (tagSet != null) {
          tags.addAll(tagSet);
        }
      }
    }
    catch (Exception e) {
      log.warn(
          "Failed to retrieve tags from capability descriptor '{}' due to {}/{}",
          descriptor, e.getClass(), e.getMessage(), log.isDebugEnabled() ? e : null
      );
    }
    try {
      if (capability instanceof Taggable) {
        Set<Tag> tagSet = ((Taggable) capability).getTags();
        if (tagSet != null) {
          tags.addAll(tagSet);
        }
      }
    }
    catch (Exception e) {
      log.warn(
          "Failed to retrieve tags from capability '{}' due to {}/{}",
          descriptor, e.getClass(), e.getMessage(), log.isDebugEnabled() ? e : null
      );
    }

    List<TagUX> tagUXs = Lists.transform(
        Lists.newArrayList(Collections2.filter(tags, Predicates.<Tag>notNull())),
        new Function<Tag, TagUX>()
        {
          @Override
          public TagUX apply(final Tag input) {
            return new TagUX().withKey(input.key()).withValue(input.value());
          }
        }
    );

    if (!tagUXs.isEmpty()) {
      capabilityStatus.setTags(tagUXs);
    }

    return capabilityStatus;
  }

  private static CapabilityUX asCapability(final CapabilityReference reference) {
    CapabilityUX capability = new CapabilityUX()
        .withId(reference.context().id().toString())
        .withNotes(reference.context().notes())
        .withEnabled(reference.context().isEnabled())
        .withTypeId(reference.context().type().toString());

    if (reference.context().properties() != null) {
      for (final Map.Entry<String, String> entry : reference.context().properties().entrySet()) {
        capability.getProperties().add(new PropertyUX().withKey(entry.getKey()).withValue(entry.getValue()));
      }
    }
    return capability;
  }

  private static Map<String, String> asMap(final List<PropertyUX> properties) {
    final Map<String, String> map = Maps.newHashMap();

    if (properties != null) {
      for (final PropertyUX property : properties) {
        map.put(property.getKey(), property.getValue());
      }
    }

    return map;
  }

}
