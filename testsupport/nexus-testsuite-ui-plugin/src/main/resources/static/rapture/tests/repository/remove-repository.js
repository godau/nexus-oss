startTest({

  waitForAppReady: false

}, function (t) {
  var numberOfRecs;
  t.chain(
      function (next) {
        t.waitForMs(1500, function () {
          next();
        });
      },
      { waitFor: 'CQVisible', args: 'nx-featurebrowser' },
      function (next, comps) {
        var fb = comps[0];

        fb.setActiveTab(1);

        next();
      },
      { waitFor: 'rowsVisible', args: 'nx-repository-list' },
      function (next) {
        var repoGrid = t.cq1('nx-repository-list'),
            deleteButton = t.cq1('nx-repository-list button[action=delete]');

        t.ok(deleteButton.isDisabled(), 'Delete button is disabled');
        repoGrid.getSelectionModel().select(0);
        t.ok(!deleteButton.isDisabled(), 'Delete button is enabled');
        numberOfRecs = repoGrid.getStore().getCount();
        next();
      },
      { click: '>>nx-repository-list button[action=delete]' },
      { waitFor: 'CQVisible', args: 'messagebox' },
      { click: '>>button[text=No]' },
      function (next) {
        var repoGrid = t.cq1('nx-repository-list');

        t.is(repoGrid.getStore().getCount(), numberOfRecs, "Selected record was not deleted");

        next();
      },
      { click: '>>nx-repository-list button[action=delete]' },
      { waitFor: 'CQVisible', args: 'messagebox' },
      { click: '>>button[text=Yes]' },
      function (next) {
        t.waitForMs(1500, function () {
          next();
        });
      },
      function (next) {
        var repoGrid = t.cq1('nx-repository-list');

        t.isLess(repoGrid.getStore().getCount(), numberOfRecs, "Selected record was deleted");

        next();
      }
  );
});
