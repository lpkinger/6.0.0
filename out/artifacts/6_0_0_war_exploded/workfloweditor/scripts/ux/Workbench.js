
Ext.ux.Workbench = function(security) {
    this.map = {};
    this.security = security;
};

Ext.ux.Workbench.prototype = {
    init: function() {
        this.initView();
        this.initLoginWindow();
    },

    initView: function() {
        this.north = this.createNorthPanel();
        this.south = this.createSouthPanel();
        this.west = this.createWestPanel();
        this.center = this.createCenterPanel();

        var viewport = new Ext.Viewport({
            layout: 'border',
            items: [
                this.north,
                this.south,
                this.west,
                this.center
            ]
        });

        setTimeout(function(){
            Ext.get('loading').remove();
            Ext.get('loading-mask').fadeOut({remove:true});
        }, 500);
    },

    initLoginWindow: function() {
        if (this.security.loginUrl) {
            this.loginWindow = new Ext.ux.LoginWindow({
                url: this.security.loginUrl,
                callback: function(result) {
                    this.rebuildMenu(result.info.menus);
                    this.loginWindow.hide();
                }.createDelegate(this)
            });


            Ext.Ajax.request({
                url: this.security.checkUrl,
                success: function(response) {
                    var result = Ext.decode(response.responseText);
                    if (result.success) {
                        this.rebuildMenu(result.info.menus);
                    } else {
                        this.loginWindow.show();
                    }
                },
                failure: function(response) {
                    Ext.Msg.alert('错误', '无法访问服务器。');
                },
                scope: this
            });
        } else {
            this.loginWindow = new Ext.ux.LoginWindow({
                url: (typeof WEB_ROOT == 'undefined' ? '.' : WEB_ROOT) + '/scripts/ux/window/login.jsp',
                callback: function(result) {
                    this.rebuildMenu(this.security);
                    this.loginWindow.hide();
                }.createDelegate(this)
            });


            Ext.Ajax.request({
                url: (typeof WEB_ROOT == 'undefined' ? '.' : WEB_ROOT) + '/scripts/ux/window/login.jsp',
                success: function(response) {
                    var result = Ext.decode(response.responseText);
                    if (result.success) {
                        this.rebuildMenu(this.security);
                    } else {
                        this.loginWindow.show();
                    }
                },
                failure: function(response) {
                    Ext.Msg.alert('错误', '无法访问服务器。');
                },
                scope: this
            });
        }
    },

    logout: function() {
        if (this.security.logoutUrl) {
            Ext.Ajax.request({
                url: this.security.logoutUrl,
                success: function(response) {
                    this.clean();
                    this.loginWindow.formPanel.getForm().reset();
                    this.loginWindow.show();
                },
                failure: function(response) {
                    Ext.Msg.alert('错误', '无法访问服务器。');
                },
                scope: this
            });
        } else {
            Ext.Ajax.request({
                url: (typeof WEB_ROOT == 'undefined' ? '.' : WEB_ROOT) + '/scripts/ux/window/logout.jsp',
                success: function(response) {
                    this.clean();
                    this.loginWindow.formPanel.getForm().reset();
                    this.loginWindow.show();
                },
                failure: function(response) {
                    Ext.Msg.alert('错误', '无法访问服务器。');
                },
                scope: this
            });
        }
    },

    createNorthPanel: function() {
        return {
            region: 'north',
            height: 80,
            margins: '5 5 5 5',
            html: '<img src="' + (typeof WEB_ROOT == 'undefined' ? '.' : WEB_ROOT) + '/scripts/ux/family168.png">',
            bbar: new Ext.Toolbar(['->', {
                text: 'Logout',
                iconCls: 'logout-btn',
                handler: function() {
                    this.logout();
                },
                scope: this
            }])
        };
    },

    createSouthPanel: function() {
        return {
            region: 'south',
            height: 18,
            margins: '5 0 0 0',
            border: false,
            frame: false,
            bodyStyle: 'background-color:#99BBEE;color:white;font-size:12px;font-weight:bold;',
            html: new Date().toString()
        };
    },

    createWestPanel: function() {
        var westPanel = new Ext.Panel({
            id: 'mainAccordion',
            region: 'west',
            title: '功能菜单',
            layout: 'accordion',
            cls: 'west-menu',
            width: 150,
            minSize: 120,
            maxSize: 200,
            split: true,
            collapsible: true,
            margins: '0 0 0 5',
            cmargins: '0 5 0 5',
            frame: true,
            border: true,
            defaults: {
                border: false,
                lines: false,
                autoScroll: true,
                bodyStyle: 'background: #fff;',
                collapseFirst: true
            }
        });
        return westPanel;
    },

    createCenterPanel: function() {
        var tabPanel = new Ext.TabPanel({
            region: 'center',
            layoutOnTabChange: true,
            enableTabScroll: true,
            monitorResize: true,
            activeTab: 0,
            margins: '0 5 0 0',
            plain: true,
            frame: true,
            hideMode: 'offsets',
            deferredRender: false,
            deferredLayout: false,
            defaults: {
                closable: true,
                viewConfig: {
                    forceFit: true
                },
                margins: '5 5 5 5',
                bodyBorder: true
            },
            items: [{
                id: 'Home',
                title: '欢迎您',
                closable: false,
                autoScroll: true,
                iconCls: 'welcome',
                html: '<table width="100%" height="100%"><tr><td align="center">'
                    + '<a href="http://www.china-pub.com/195152" target="_blank">'
                    + '<img src="' + (typeof WEB_ROOT == 'undefined' ? '.' : WEB_ROOT) + '/scripts/ux/cover.jpg" width="300" height="380" border="0" />'
                    + '</a></td></tr></table>'
            }]
        });

        return tabPanel;
    },

    register: function(name, fn, iconCls, notConstructor) {
        if (fn !== null && typeof fn !== 'function') {
            console.error(fn + ' must be a function for ' + name + '.');
        }
        App.view.map[name] = {
            fn: fn,
            iconCls: iconCls,
            notConstructor: notConstructor
        };
    },

    openTab: function(name) {
        var comp = this.map[name];
        if (typeof comp === 'undefined') {
            return;
        }
        if (comp.notConstructor === true) {
            comp.fn.call(this);
        } else {
            var tabs = this.center;
            var tabItem = tabs.getItem(name);
            if (tabItem == null) {
                var c = comp.fn;
                var p = new c;
                tabItem = tabs.add(p);
            }
            tabs.activate(tabItem);
        }
    },

    rebuildMenu: function(info) {
        var mainAccordion = this.west;
        for (var i = 0; i < info.length; i++) {
            var title = info[i].text;
            var comp = this.map[info[i].content];
            if (typeof comp === 'undefined') {
                continue;
            }
            var iconCls = comp.iconCls;

            for (var j = 0; j < info[i].children.length; j++) {
                var item = info[i].children[j];
                var child = this.map[item.content];
                if (typeof child !== 'undefined') {
                    item.iconCls = child.iconCls;
                }
            }

            var p = new Ext.tree.TreePanel({
                title: title,
                iconCls: iconCls,
                rootVisible: false,
                loader: new Ext.tree.TreeLoader(),
                root: new Ext.tree.AsyncTreeNode({
                    text:'功能菜单',
                    children: info[i].children
                })
            });
            p.on('click', function(node) {
                this.openTab(node.attributes.content);
            }, this);
            mainAccordion.add(p);
        }
        mainAccordion.doLayout();
    },

    clean: function() {
        var tabs = this.center;
        for (var i = tabs.items.length; i > 0; i--) {
            var tab = tabs.getComponent(i);
            tabs.remove(tab, true);
        }
        this.west.removeAll();
    },

    notExists: function(id) {
        var tabItem = this.center.getItem(id);
        if (tabItem) {
            this.center.activate(tabItem);
            return false;
        } else {
            return true;
        }
    },

    openPanel: function(p) {
        var tabItem = this.center.add(p);
        this.center.activate(tabItem);
    },

    getPanel: function(id) {
        return this.center.getItem(id);
    },

    addPanel: function(conf) {
        var tabItem = this.center.getItem(conf.id);
        if (tabItem) {
            tabItem = new conf.fn;
            this.center.add(tabItem);
        }
        conf.callback.call(window, tabItem);
        this.center.activate(tabItem);
    }
};
