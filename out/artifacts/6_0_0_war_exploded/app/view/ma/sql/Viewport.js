Ext.define('erp.view.ma.sql.Viewport', {
    extend: 'Ext.container.Viewport',
    layout:'fit',
    id:'Viewport',
    requires: [
        'erp.view.ma.sql.ResultTab',
        'erp.view.ma.sql.Form'
    ],

    initComponent: function() {
        this.items = {
            layout: 'border',
            items: [{
              xtype:'result-form',
              region:'north'
            },{
              xtype: 'resulttab',
              region:'center'
            }]
        };
        this.callParent();
    }
});