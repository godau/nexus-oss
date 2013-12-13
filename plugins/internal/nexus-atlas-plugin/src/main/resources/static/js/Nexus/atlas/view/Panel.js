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

/**
 * Atlas (support tools) panel.
 *
 * @since 2.7
 */
NX.define('Nexus.atlas.view.Panel', {
  extend: 'Ext.Panel',

  mixins: [
    'Nexus.LogAwareMixin'
  ],

  requires: [
    'Nexus.atlas.Icons',
    'Nexus.atlas.view.SysInfo',
    'Nexus.atlas.view.SupportZip'
  ],

  xtype: 'nx-atlas-view-panel',
  title: 'Support Tools',
  cls: 'nx-atlas-view-panel',

  border: false,
  layout: {
    type: 'vbox',
    align: 'stretch'
  },

  /**
   * @override
   */
  initComponent: function() {
    var me = this,
        icons = Nexus.atlas.Icons;

    Ext.apply(me, {
      items: [
        {
          xtype: 'panel',
          cls: 'nx-atlas-view-panel-description',
          border: false,
          html: icons.get('atlas').variant('x32').img +
              '<div>Support tools provides a collection of modules to help keep your server healthy.</div>',
          height: 60,
          flex: 0
        },
        {
          xtype: 'tabpanel',
          flex: 1,
          border: false,
          plain: true,
          layoutOnTabChange: true,
          items: [
            { xtype: 'nx-atlas-view-sysinfo' },
            { xtype: 'nx-atlas-view-supportzip' }
          ],
          activeTab: 0
        }
      ]
    });

    me.constructor.superclass.initComponent.apply(me, arguments);
  }
});