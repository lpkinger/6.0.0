
Ext.ns('App');

App.Engine = function(modules) {
    this.map = {};
    this.setup(modules);
};

App.Engine.prototype = {
    setup: function(modules) {
        modules = modules ? modules : {};

        for (var key in modules) {
            var obj = modules[key];
            this.reg(key, obj);
        }
        this.createSecurity(modules.security);

        this.init();
    },

    reg: function(key, obj) {
        this.map[key] = {
            title: obj.title,
            content: key,
            fn: null,
            iconCls: obj.iconCls,
            notConstructor: true,
            items: []
        };
        for (var i = 0; i < obj.items.length; i++) {
            var item = obj.items[i];
            if (typeof item == 'function') {
                this.map[key].items.push({
                    content: item.prototype.id,
                    fn: item,
                    iconCls: item.prototype.iconCls,
                    notConstructor: false,
                    title: item.prototype.title
                });
            } else if (typeof item == 'object') {
                this.map[key].items.push({
                    content: item.content,
                    fn: item.fn,
                    iconCls: item.iconCls,
                    notConstructor: item.notConstructor,
                    title: item.title
                });
            }
        }
    },

    createSecurity: function(security) {
        if (!security) {
            security = [];
            for (var key in this.map) {
                var obj = this.map[key];
                var parent = {
                    text: obj.title,
                    leaf: false,
                    content: key,
                    children: []
                };
                for (var i = 0; i < obj.items.length; i++) {
                    var item = obj.items[i];
                    parent.children.push({
                        text: item.title,
                        leaf: true,
                        content: item.content
                    });
                }
                security.push(parent);
            }
        }

        App.view = new Ext.ux.Workbench(security);
    },

    init: function() {
        for (var key in this.map) {
            var obj = this.map[key];
            App.view.register(obj.content, obj.fn, obj.iconCls, obj.notConstructor);
            for (var i = 0; i < obj.items.length; i++) {
                var item = obj.items[i];
                App.view.register(item.content, item.fn, item.iconCls, item.notConstructor);
            }
        }
        App.view.init();
    }
}

