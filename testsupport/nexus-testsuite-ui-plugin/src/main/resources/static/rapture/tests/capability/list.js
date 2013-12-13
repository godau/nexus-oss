startTest(function (t) {

  t.requireOk(
      [
        'NX.capability.model.CapabilityStatus',
        'NX.capability.view.List'
      ],
      function () {
        var grid = Ext.create('NX.capability.view.List', {
          renderTo: Ext.getBody(),
          store: Ext.create('Ext.data.Store', {
            autoLoad: true,
            model: 'NX.capability.model.CapabilityStatus',
            data: [
              {
                enabled: true,
                active: true,
                typeId: 'foo',
                typeName: 'Foo capability',
                description: 'Some desc',
                status: null,
                stateDescription: 'Active',
                notes: null,
                tags: [
                  {
                    "key": "Category",
                    "value": "Security"
                  }
                ]
              }
            ],
            proxy: {
              type: 'memory',
              reader: {
                type: 'json'
              }
            }
          }),
          height: 200,
          width: 300
        });

        grid.store.load();

        t.waitForRowsVisible(grid, function () {
          t.is(grid.store.getCount(), grid.getView().getNodes().length, 'Rendered all data in store ok');
          t.matchGridCellContent(grid, 0, 0, 'Foo capability', 'Found type name in first cell');
        });
      }
  );

});
