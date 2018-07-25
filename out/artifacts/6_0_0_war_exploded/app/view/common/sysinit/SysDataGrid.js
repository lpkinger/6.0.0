Ext.define('erp.view.common.sysinit.SysDataGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.sysdatagrid',
	autoScroll: true,
	manageHeight: false,
	defaultType:'panel',
	store:Ext.create('Ext.data.Store', {
        fields:['in_desc','in_isrequired'],
	    autoLoad: true
	}),
	columns:[{
		header:'数据项',
		dataIndex:'in_desc',
		flex:1,
		renderer:function(val,record){
			if(record['in_isrequired']!=0)
				return  val+'<font color="red">*</font>';
			return val;
		}
	},{
		header:'已记录数',
		dataIndex:'count'
	},{
        menuDisabled: true,
        sortable: false,
        xtype: 'actioncolumn',
        width: 50,
        items: [{
            iconCls: 'sell-col',
            tooltip: 'Sell stock',
            handler: function(grid, rowIndex, colIndex) {
                var rec = grid.getStore().getAt(rowIndex);
                Ext.Msg.alert('Sell', 'Sell ' + rec.get('company'));
            }
        }, {
            getClass: function(v, meta, rec) {
                if (rec.get('change') < 0) {
                    return 'alert-col';
                } else {
                    return 'buy-col';
                }
            },
            getTip: function(v, meta, rec) {
                if (rec.get('change') < 0) {
                    return 'Hold stock';
                } else {
                    return 'Buy stock';
                }
            },
            handler: function(grid, rowIndex, colIndex) {
                var rec = grid.getStore().getAt(rowIndex),
                    action = (rec.get('change') < 0 ? 'Hold' : 'Buy');

                Ext.Msg.alert(action, action + ' ' + rec.get('company'));
            }
        }]
    }],
    listeners:{
     afterrender:function(grid){
    	 grid.loadStore();
     }	
    },
	initComponent : function() {
		var me = this;
		this.callParent();
	},
	loadStore:function(){
		var syspanel =parent.Ext.getCmp('syspanel'),nodes=syspanel.currentRecord.childNodes;
	}
	
});
