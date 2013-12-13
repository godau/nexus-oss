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
/*global NX, Ext, Nexus*/

/**
 * Container for icons used by wonderland plugin.
 *
 * @since 2.7
 */
NX.define('Nexus.wonderland.Icons', {
  extend: 'Nexus.util.IconContainer',
  singleton: true,

  /**
   * @constructor
   */
  constructor: function () {
    var me = this;

    // helper to build an icon config with variants, where variants live in directories, foo.png x16 -> x16/foo.png
    function iconConfig(fileName, variants) {
      var config = {};
      if (variants === undefined) {
        variants = [ 'x32', 'x16' ]
      }
      Ext.each(variants, function(variant) {
        config[variant] = variant + '/' + fileName;
      });
      return config;
    }

    me.constructor.superclass.constructor.call(me, {
      stylePrefix: 'nx-wonderland-icon-',

      icons: {
        lock: iconConfig('lock.png')
      }
    });
  }

});