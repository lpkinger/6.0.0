
Ext.ns('App.property');

App.property.AbstractPropertyPanel = Ext.extend(Ext.Panel, {
    title: '属性面板',
    iconCls: 'tb-prop',
    layout: 'fit',
    split: true,

    tools: [{
        id: 'maximize',
        handler: function(event, toolEl, panel) {
            panel.propertyManager.changePropertyStatus('max');
        }
    }],

    // ========================================================================

    initComponent: function() {
        var tabPanel = new Ext.TabPanel({
            enableTabScroll:true,
            layoutOnTabChange: true,
            defaults: {
                autoScroll: true
            }
        });
        this.tabPanel = tabPanel;
        this.items = [tabPanel];
        App.property.AbstractPropertyPanel.superclass.initComponent.call(this);
    },

    setPropertyManager: function(propertyManager) {
        this.propertyManager = propertyManager;
    },

    getTabPanel: function() {
        return this.tabPanel;
    },

    hide: function() {
        this.clearItem(this.tabPanel);
        App.property.AbstractPropertyPanel.superclass.hide.call(this);
    },

    clearItem: function(p) {
        if (typeof p.items != 'undefined') {
            var item = null;
            while ((item = p.items.last())) {
                p.remove(item, true);
            }
        }
    }
});

Ext.ns('App.property');

App.property.BottomPanel = Ext.extend(App.property.AbstractPropertyPanel, {
    region: 'south',
    height: 200,

    draggable: {
        insertProxy: false,
        onDrag: function(e) {
            var pel = this.proxy.getEl();
            this.x = pel.getLeft(true);
            this.y = pel.getTop(true);
        },
        endDrag: function(e) {
            var x = this.x;
            var y = this.y;
            var propertyManager = this.panel.propertyManager;
            var size = Ext.getBody().getViewSize();

            if (y < size.height - 200) {
                if (x > size.width - 200) {
                    propertyManager.changePropertyStatus('right');
                } else {
                    propertyManager.changePropertyStatus('max');
                }
            }
        }
    },

    getStatusName: function() {
        return 'bottom';
    }
});


Ext.ns('App.property');

App.property.MaxWindow = Ext.extend(Ext.Window, {
    title: '属性面板',
    iconCls: 'tb-prop',
    layout: 'fit',
    stateful: false,

    closable: false,
    width: 500,
    height: 400,
    // FIXME: 希望实现，不disable editor，编辑window中的元素时，不会选中editor中的元素
    modal: false,
    constrainHeader: true,
    autoScroll: true,

    tools: [{
        id: 'restore',
        handler: function(event, toolEl, panel) {
            panel.propertyManager.changePropertyStatus(panel.restore.getStatusName());
        }
    }],

    // ========================================================================

    initComponent: function() {
        var tabPanel = new Ext.TabPanel({
            enableTabScroll:true,
            layoutOnTabChange: true
        });
        this.tabPanel = tabPanel;
        this.items = [tabPanel];
        App.property.MaxWindow.superclass.initComponent.call(this);
    },

    afterRender: function() {
        App.property.MaxWindow.superclass.afterRender.call(this);

        this.dd.endDrag = function(e) {
            try {
                this.win.unghost();
                // this.win.saveState();

                var x = e.xy[0];
                var y = e.xy[1];
                var propertyManager = this.win.propertyManager;
                var size = Ext.getBody().getViewSize();

                if (y > size.height - 200) {
                    propertyManager.changePropertyStatus('bottom');
                } else if (x > size.width - 200) {
                    propertyManager.changePropertyStatus('right');
                }
            } catch(e) {
                Gef.error(e);
            }
        }.createDelegate(this.dd);
    },

    setPropertyManager: function(propertyManager) {
        this.propertyManager = propertyManager;
    },

    getTabPanel: function() {
        return this.tabPanel;
    },

    clearItem: function(p) {
        if (typeof p.items != 'undefined') {
            var item = null;
            while ((item = p.items.last())) {
                p.remove(item, true);
            }
        }
    },

    // ========================================================================

    hide: function() {
        this.clearItem(this.tabPanel);

        if (this.el) {
            if (Gef.activeEditor) {
                //Gef.activeEditor.enable();
            }
            App.property.MaxWindow.superclass.hide.call(this);
        }
    },

    show: function() {
        if (Gef.activeEditor) {
            //Gef.activeEditor.disable();
        }
        delete this.x;
        delete this.y;
        App.property.MaxWindow.superclass.show.call(this);
    },

    getStatusName: function() {
        return 'max';
    },

    setRestore: function(restore) {
        this.restore = restore;
    }
});




Ext.ns('App.property');

App.property.PropertyManager = Ext.extend(Object, {
    constructor: function() {
        this.bottomPanel = new App.property.BottomPanel();
        this.bottomPanel.setPropertyManager(this);

        this.rightPanel = new App.property.RightPanel();
        this.rightPanel.setPropertyManager(this);

        this.maxWindow = new App.property.MaxWindow();
        this.maxWindow.setPropertyManager(this);

        var propertyStatus = Cookies.get('_gef_jbpm4_property_status');
        if (propertyStatus != 'bottom') {
            propertyStatus = 'right';
        }
        this.changePropertyStatus(propertyStatus);

        this.initMap();
    },

    changePropertyStatus: function(status) {
        try {
            status = status ? status : 'right';
            Cookies.set('_gef_jbpm4_property_status', status);

            switch (status) {
                case 'right':
                    this.current = this.rightPanel;
                    this.current.show();
                    if (this.form) {
                        this.form.decorate(this.current.getTabPanel(), this.model);
                    }

                    this.maxWindow.hide();
                    this.bottomPanel.hide();
                    if (this.rightPanel.ownerCt) {
                        this.rightPanel.ownerCt.doLayout();
                    }
                    break;
                case 'bottom':
                    this.current = this.bottomPanel;
                    this.current.show();
                    if (this.form) {
                        this.form.decorate(this.current.getTabPanel(), this.model);
                    }

                    this.maxWindow.hide();
                    this.rightPanel.hide();
                    if (this.rightPanel.ownerCt) {
                        this.rightPanel.ownerCt.doLayout();
                    }
                    break;
                case 'max':
                    this.maxWindow.setRestore(this.current);
                    this.current = this.maxWindow;
                    this.current.show();
                    if (this.form) {
                        this.form.decorate(this.current.getTabPanel(), this.model);
                    }

                    this.bottomPanel.hide();
                    this.rightPanel.hide();
                    if (this.rightPanel.ownerCt) {
                        this.rightPanel.ownerCt.doLayout();
                    }
                    break;
            }
        } catch(e) {
            Gef.error(e);
        }
    },

    getBottom: function() {
        return this.bottomPanel;
    },

    getRight: function() {
        return this.rightPanel;
    },

    getMax: function() {
        return this.max;
    },

    getCurrent: function() {
        return this.current;
    },

    getSelectionListener: function() {
        return this.selectionListener;
    },

    initMap: function() {
        this.formMap = {
            process:      App.form.ProcessForm,
            start:        App.form.StartForm,
            end:          App.form.EndForm,
            cancel:       App.form.CancelForm,
            error:        App.form.ErrorForm,
            state:        App.form.StateForm,
            task:         App.form.TaskForm,
            decision:     App.form.DecisionForm,
            fork:         App.form.ForkForm,
            join:         App.form.JoinForm,
            java:         App.form.JavaForm,
            script:       App.form.ScriptForm,
            hql:          App.form.HqlForm,
            sql:          App.form.SqlForm,
            mail:         App.form.MailForm,
            custom:       App.form.CustomForm,
            subProcess:   App.form.SubProcessForm,
            transition:   App.form.TransitionForm,
            jms:          App.form.JmsForm,
            ruleDecision: App.form.RuleDecisionForm,
            rules:        App.form.RulesForm,
            auto:         App.form.AutoForm,
            human:          App.form.HumanForm,
            'counter-sign': App.form.CounterSignForm,
            foreach:      App.form.ForeachForm
        };
    },

    updateForm: function(model) {
	    return;
        this.model = model;
        var modelType = model.getType();
        var constructor = this.formMap[modelType];
        if (!constructor) {
            Gef.debug('cannot find form for [' + modelType + ']',
                'App.property.PropertyManager.updateForm()');
        }
        this.form = new constructor;
        this.form.decorate(this.current.getTabPanel(), model);
    },

    initSelectionListener: function(editor) {
        this.selectionListener = new Gef.jbs.ExtSelectionListener(this);
        editor.addSelectionListener(this.selectionListener);
        this.selectionListener.setEditor(editor);

        var model = this.selectionListener.getModel();
        this.updateForm(model);
    }
});

var Cookies = {};
Cookies.set = function(name, value){
     var argv = arguments;
     var argc = arguments.length;
     var expires = (argc > 2) ? argv[2] : null;
     var path = (argc > 3) ? argv[3] : '/';
     var domain = (argc > 4) ? argv[4] : null;
     var secure = (argc > 5) ? argv[5] : false;
     document.cookie = name + "=" + escape (value) +
       ((expires == null) ? "" : ("; expires=" + expires.toGMTString())) +
       ((path == null) ? "" : ("; path=" + path)) +
       ((domain == null) ? "" : ("; domain=" + domain)) +
       ((secure == true) ? "; secure" : "");
};

Cookies.get = function(name){
    var arg = name + "=";
    var alen = arg.length;
    var clen = document.cookie.length;
    var i = 0;
    var j = 0;
    while(i < clen){
        j = i + alen;
        if (document.cookie.substring(i, j) == arg)
            return Cookies.getCookieVal(j);
        i = document.cookie.indexOf(" ", i) + 1;
        if(i == 0)
            break;
    }
    return null;
};

Cookies.clear = function(name) {
  if(Cookies.get(name)){
    document.cookie = name + "=" +
    "; expires=Thu, 01-Jan-70 00:00:01 GMT";
  }
};

Cookies.getCookieVal = function(offset){
   var endstr = document.cookie.indexOf(";", offset);
   if(endstr == -1){
       endstr = document.cookie.length;
   }
   return unescape(document.cookie.substring(offset, endstr));
};

Ext.ns('App.property');

App.property.RightPanel = Ext.extend(App.property.AbstractPropertyPanel, {
    region: 'east',
    width: 200,
    frame:true,
    draggable: {
        insertProxy: false,
        onDrag: function(e) {
            var pel = this.proxy.getEl();
            this.x = pel.getLeft(true);
            this.y = pel.getTop(true);
        },
        endDrag: function(e) {
            var x = this.x;
            var y = this.y;
            var propertyManager = this.panel.propertyManager;
            var size = Ext.getBody().getViewSize();

            if (x < size.width - 200) {
                if (y > size.height - 200) {
                    propertyManager.changePropertyStatus('bottom');
                } else {
                    propertyManager.changePropertyStatus('max');
                }
            }
        }
    },

    getStatusName: function() {
        return 'right';
    }
});
