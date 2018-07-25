Ext.define('erp.view.oa.doc.DocLog', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.erpDocLogPanel',
	id:'doclogPanel',
	dlId: 0,
	columnLines : true,
	autoScroll : true,
	scroll:true, 
	resizable:false,
	viewConfig: {
		style: { overflow: 'auto', overflowX: 'hidden' }
	},
	columns: [{
		text:'时间',
		dataIndex:'ML_DATE',
		width:50,
		hidden:false,
		renderer:function(value){
			return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
		},
		flex: 1
	},{
		text:'操作人员',
		dataIndex:'ML_MAN',
		width:50,
		hidden:false,
		flex: 1
	},{
		text:'操作',
		dataIndex:'ML_CONTENT',
		width:50,
		hidden:false,
		flex: 1
	},{
		text:'结果',
		dataIndex:'ML_RESULT',
		width:50,
		hidden:false,
		flex: 1
	}],
	initComponent: function(){
		var me = this;
		me.store = Ext.create('Ext.data.Store',{
			fields: [{
				name: 'ML_DATE',
				type: 'int'
			},{
				name: 'ML_MAN',
				type: 'string'
			},{
				name: 'ML_CONTENT',
				type: 'string'
			},{
				name: 'ML_RESULT',
				type: 'string'
			}],
			proxy: {
				type: 'ajax',
				url: basePath + 'oa/doc/getDocLog.action',
				extraParams:{
					docId: me.dlId
		        },
				reader: {
		            type: 'json',
		        }
			},
			autoLoad: false
		});
		me.callParent(arguments);
	},
	listeners: {
		'afterrender': function(me){
			me.store.load();
		}
	}
});