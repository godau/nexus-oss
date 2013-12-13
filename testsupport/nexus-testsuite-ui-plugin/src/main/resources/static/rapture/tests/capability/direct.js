startTest(function (t) {
  t.diag("Sanity test, loading classes on demand and verifying they were indeed loaded.");

  t.ok(NX.direct.Capability, "Capability Ext.Direct is defined");
  t.ok(NX.direct.CapabilityType, "CapabilityType Ext.Direct is defined");

});
