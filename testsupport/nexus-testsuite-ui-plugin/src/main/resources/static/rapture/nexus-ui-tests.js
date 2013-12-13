var Harness = Siesta.Harness.Browser.ExtJS;

Harness.configure({
  title: 'Nexus UI Suite',
  viewDOM: true,

  preload: [
    "resources/baseapp-debug.css",
    "resources/rapture.css",
    "baseapp-debug.js",
    "/nexus/service/rapture/direct"
  ]
});

Harness.start(
    {
      group: 'Capability',
      items: [
        'tests/capability/directjengine.js',
        'tests/capability/list.js',
        'tests/capability/sanity.js'
      ]
    },
    {
      group: 'Repository',
      items: [
        'tests/repository/directjengine.js',
        'tests/repository/sanity.js',
        {
          preload: [],
          hostPageUrl: 'index.html',
          url: 'tests/repository/remove-repository.js'
        }
      ]
    }
);
