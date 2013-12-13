startTest(function (t) {
  t.diag("Sanity test, loading classes on demand and verifying they were indeed loaded.");

  t.requireOk('NX.capability.app.PluginConfig');
  t.requireOk('NX.capability.controller.Capabilities');
  t.requireOk('NX.capability.model.Capability');
  t.requireOk('NX.capability.model.CapabilityStatus');
  t.requireOk('NX.capability.model.CapabilityType');
  t.requireOk('NX.capability.store.Capability');
  t.requireOk('NX.capability.store.CapabilityStatus');
  t.requireOk('NX.capability.store.CapabilityType');
  t.requireOk('NX.capability.view.About');
  t.requireOk('NX.capability.view.Add');
  t.requireOk('NX.capability.view.List');
  t.requireOk('NX.capability.view.Settings');
  t.requireOk('NX.capability.view.SettingsFieldSet');
  t.requireOk('NX.capability.view.Status');
  t.requireOk('NX.capability.view.Summary');

});
