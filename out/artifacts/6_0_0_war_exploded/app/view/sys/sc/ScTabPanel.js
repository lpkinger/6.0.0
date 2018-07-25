Ext.define('erp.view.sys.sc.ScTabPanel',{
	extend: 'Ext.tab.Panel', 
	alias: 'widget.sctabpanel',
	id:'sctabpanel',
	animCollapse: false,
	bodyBorder: false,
	border: false,
	autoShow: true, 
	tabPosition:'bottom',
	frame:true,
	dockedItems: [Ext.create('erp.view.sys.base.Toolbar')],
	defaults:{
	    plugins: [{
	        ptype: 'cellediting',
	        clicksToEdit: 2,
	        pluginId: 'cellplugin'
	    }]
	},
	items: [{
		title:'仓位',
		xtype:'simpleactiongrid',
		caller:'ProductLocation',
		saveUrl: 'common/saveCommon.action?caller=ProductLocation',
		deleteUrl: 'common/deleteCommon.action?caller=ProductLocation',
		updateUrl: 'common/updateCommon.action?caller=ProductLocation',
		getIdUrl: 'common/getId.action?caller=ProductLocation',
		keyField: 'pl_id',
		autoRender:true,
		statusField:'pl_status',
		statusCodeField:'pl_statuscode',
		params:{
			caller:'ProductLocation!Grid',
			condition:'1=1'
		}
	},{
		title: '仓库资料',
		xtype:'simpleactiongrid',
		caller:'Warehouse!Base',
		saveUrl: 'common/saveCommon.action?caller=Warehouse!Base',
		deleteUrl: 'common/deleteCommon.action?caller=Warehouse!Base',
		updateUrl: 'common/updateCommon.action?caller=Warehouse!Base',
		getIdUrl: 'common/getCommonId.action?caller=Warehouse!Base',
		keyField:'wh_id',
		autoRender:true,
		params:{
			caller:'Warehouse!Base!Grid',
			condition:'1=1'
		}
	},{
		title:'其它入库类型',
		xtype:'combosetgrid',
		plugins:[Ext.create('Ext.grid.plugin.CellEditing', {
			clicksToMoveEditor: 1,        
			autoCancel: false
		})],
		animCollapse: true,
		collapsible: true,
		width:235,
		fieldWidth:150,
		margins: '0 5 0 0',
		caller:'ProdInOut!OtherIn',
		field:'pi_type'
	},{
		title:'其它出库类型',
		xtype:'combosetgrid',
		plugins:[Ext.create('Ext.grid.plugin.CellEditing', {
			clicksToMoveEditor: 1,        
			autoCancel: false
		})],
		animCollapse: true,
		collapsible: true,
		width:235,
		fieldWidth:150,
		margins: '0 5 0 0',
		caller:'ProdInOut!OtherOut',
		field:'pi_type'
	}],
	initComponent : function(){ 
		this.callParent(arguments);
	}
});