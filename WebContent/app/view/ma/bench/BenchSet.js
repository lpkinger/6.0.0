Ext.define('erp.view.ma.bench.BenchSet', {
	extend : 'Ext.Viewport',
	layout : 'border',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
				region : 'west',
				width : 230,
				header:false,
				hideHeaders:true,
				columnLines:false,
				xtype : 'gridpanel',
				id : 'benchlist',
				store : Ext.create('Ext.data.Store', {
			   		fields:['bc_id','bc_code','bc_title','bc_detno','bc_desc','bc_used','bc_urlcond']
				}),
				columns:[{
						dataIndex : 'bc_title',
						flex:1
					}],
				tbar: [{
					xtype: 'button',
					text:$I18N.common.button.erpAddButton,
					id:'addbench',
					iconCls:'x-button-icon-addgroup'					
				},{
					xtype : 'button',
					text : $I18N.common.button.erpChangeButton,
					id : 'updatebench',
					iconCls : 'x-button-icon-modify',
					disabled : true,
					style : 'margin-left:10px'
				},{
					xtype : 'button',
					id : 'deletebench',
					iconCls: 'tree-delete',
					disabled : true,
					text: $I18N.common.button.erpDeleteButton,
					style : 'margin-left:10px'
				}]
			}, {
				region : 'center',
				xtype : 'erpBenchPanel'
			}]
		});
		me.callParent(arguments);
	}
});