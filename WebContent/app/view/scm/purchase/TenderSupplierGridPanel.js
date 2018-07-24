Ext.QuickTips.init();

Ext.define('erp.view.scm.purchase.TenderSupplierGridPanel',{
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpTenderSupplierGridPanel',
	id:'supplierGrid',
	GridUtil: Ext.create('erp.util.GridUtil'),
	plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit : 1
	}),Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	autoScroll : true, 
	columnLines : true,
	initComponent : function(){
		var me = this;
		if(Ext.isEmpty(me.griddata)){
			me.griddata = getEmptyData();
		}
		var store = Ext.create('Ext.data.Store', {
			fields: ['uu', 'enName','enBussinessCode','enAddress','contact','contactTel','contactEmail'],
			data:me.griddata
		});
		me.store = store;
		this.callParent(arguments);
	},
	necessaryFields:['uu','contact','contactTel'],
	bbar: {xtype:'toolbar',
		items: [{
			xtype : 'tbtext',
			name : 'row'
		},{
			xtype : 'button',
			id:'deletedetail',
			iconCls: 'x-button-icon-close',
	    	cls: 'x-btn-tb',
	    	tooltip: $I18N.common.button.erpDeleteDetailButton,
	    	disabled: true
		}, {
			xtype : 'copydetail'
		}, {
			xtype : 'pastedetail'
		}, {
			xtype : 'updetail'
		}, {
			xtype : 'downdetail'
		}]
	},
	dbfinds:[{
		dbGridField:'ve_name',
		field:'enName'
	},{
		dbGridField:'ve_uu',
		field:'uu'
	},{
		dbGridField:'ve_webserver',
		field:'enBussinessCode'
	},{
		dbGridField:'ve_add1',
		field:'enAddress'
	},{
		dbGridField:'ve_contact',
		field:'contact'
	},{
		dbGridField:'ve_tel',
		field:'contactTel'
	},{
		dbGridField:'ve_name',
		field:'contact'
	},{
		dbGridField:'ve_mobile',
		field:'contactTel'
	},{
		dbGridField:'ve_email',
		field:'contactEmail'
	},{
		dbGridField:'vc_name',
		field:'contact'
	},{
		dbGridField:'vc_mobile',
		field:'contactTel'
	},{
		dbGridField:'vc_officeemail',
		field:'contactEmail'
	}],
	columns:[{
		header:'序号',
		xtype:'rownumberer',
		cls : 'x-grid-header-1',
		align:'center',
		width:35
	},{
		header:'企业名',
		dataIndex:'enName',
		width:200,
		dbfind: "Supplier|ve_name",
		cls : 'x-grid-header-1',
		logic:'ignore',
		editor: {
	        xtype: "multidbfindtrigger"
	    }
	},{
		header:'企业UU',
		dataIndex:'uu',
		style:'color:rgb(191, 60, 60)',
		cls : 'x-grid-header-1',
		width:120
	},{
		header:'营业执照号',
		dataIndex:'enBussinessCode',
		cls : 'x-grid-header-1',
		logic:'ignore',
		width:150
	},{
		header:'注册地址',
		dataIndex:'enAddress',
		cls : 'x-grid-header-1',
		logic:'ignore',
		width:300
	},{
		header:'联系人',
		dataIndex:'contact',
		style:'color:rgb(191, 60, 60)',
		cls : 'x-grid-header-1',
		dbfind: "VendorContact|vc_name",
		editor: {
	        xtype: "dbfindtrigger",
	        name:'contact'
	    },
		width:100
	},{
		header:'联系人电话',
		style:'color:rgb(191, 60, 60)',
		dataIndex:'contactTel',
		cls : 'x-grid-header-1',
		width:120
	},{
		header:'联系人邮箱',
		dataIndex:'contactEmail',
		cls : 'x-grid-header-1',
		width:120
	}]
});