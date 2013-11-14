Ext.define('NX.app.Application', {
  extend: 'Ext.app.Application',

  requires: [
    'Ext.Direct',
    'Ext.state.Manager',
    'Ext.state.CookieProvider',
    'Ext.state.LocalStorageProvider',
    'Ext.util.LocalStorage',
    'NX.view.Viewport'
  ],

  mixins: {
    logAware: 'NX.LogAware'
  },

  name: 'NX',
  appFolder: 'NX',

  namespaces: [],
  controllers: [
    'Main',
    'MasterDetail'
  ],
  models: [],
  refs: [],
  stores: [],
  views: [],

  constructor: function (config) {
    var self = this, custom, keys;

    // only these customizations will be allowed
    custom = {
      namespaces: self.namespaces,
      controllers: self.controllers,
      models: self.models,
      refs: self.refs,
      stores: self.stores,
      views: self.views
    };
    keys = Object.keys(custom);
    self.logDebug('Supported customizations: ' + keys);

    // TODO: More error handling around pluginConfigClassNames content, this needs to be defined, should have at least one element, etc

    // for each plugin, merge its customizations
    self.logDebug('Plugins config class names: ' + NX.app.pluginConfigClassNames);
    Ext.each(NX.app.pluginConfigClassNames, function (className) {
      var pluginConfig;

      self.logDebug('Loading plugin config from class: ' + className);
      pluginConfig = Ext.create(className);

      // Detect customizations, these are simply fields defined on the plugin object
      // supported types are Array and String only
      Ext.each(keys, function (key) {
        var value = pluginConfig[key];
        if (value) {
          self.logDebug(key + ': ' + value);
          if (Ext.isString(value)) {
            custom[key].push(value);
          }
          else if (Ext.isArray(value)) {
            custom[key] = custom[key].concat(value);
          }
          else {
            Ext.Error.raise('Invalid customization; class: ' + className + ', property: ' + key);
          }
        }
      });
    });

    // apply the customization to this application
    self.logDebug('Applying customizations');

    Ext.each(keys, function (key) {
      self.logDebug(key + ': ' + custom[key]);
    });
    Ext.apply(self, custom);

    // Have to manually add namespaces, this is done by onClassExtended in super not in parent call
    if (custom.namespaces) {
      Ext.app.addNamespaces(custom.namespaces);
    }

    // and then let the super-class do the real work
    self.callParent(arguments);
  },

  init: function (app) {
    app.initDirect();
    app.initState();
  },

  initDirect: function () {
    //Ext.Direct.addProvider(NX.direct.api.REMOTING_API);
    this.logDebug('Configured direct');
  },

  initState: function () {
    var self = this, provider;

    // prefer local storage if its supported
    if (Ext.util.LocalStorage.supported) {
      provider = Ext.create('Ext.state.LocalStorageProvider');
      this.logDebug('Using state provider: local');
    }
    else {
      provider = Ext.create('Ext.state.CookieProvider');
      this.logDebug('Using state provider: cookie');
    }

    // HACK: for debugging
    provider.on('statechange', function (provider, key, value, opts) {
      self.logDebug('State changed: ' + key + '=' + value);
    });

    Ext.state.Manager.setProvider(provider);
  },

  launch: function (profile) {
    Ext.create('NX.view.Viewport');

    // hide the loading mask after we have loaded
    var hideMask = function () {
      Ext.get('loading').remove();
      Ext.fly('loading-mask').animate({
        opacity: 0,
        remove: true
      });
    };

    Ext.defer(hideMask, 250);
  }
});