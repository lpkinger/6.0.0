Ext.QuickTips.init();

Ext.define('erp.view.scm.purchase.TenderProductGridPanel',{
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpTenderProductGridPanel',
	id:'productGrid',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
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
			fields: [{name:'id',type:'long'},{name:'index',type:'int'}, {name:'prodCode',type:'string'},{name:'brand',type:'string'},{name:'prodTitle',type:'string'}, {name:'brand',type:'string'},{name:'unit',type:'string', defaultValue: 'PCS'}, {name:'qty',type:'long'}],
			data:me.griddata
		});
		me.store = store;
		me.store.sort({
	        property : 'index',
	        direction: 'ASC'
	    });
		this.callParent(arguments);
	},
	griddata:null,
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
		}, {
			xtype : 'erpExportDetailButton'
		}]
	},
	necessaryFields:['prodTitle','qty'],
	dbfinds:[{
		dbGridField:'pr_detail',
		field:'prodTitle'
	},{
		dbGridField:'pr_spec',
		field:'prodCode'
	},{
		dbGridField:'pr_unit',
		field:'unit'
	},{
		dbGridField:'pr_brand',
		field:'brand'
	},{
		dbGridField:'pb_name',
		field:'brand'
	}],
	columns:[{
		header:'ID',
		dataIndex:'id',
		width:80,
		hidden:true
	},{
		header:'序号',
		dataIndex:'index',
		xtype:'numbercolumn',
		align:'center',
		cls : 'x-grid-header-1',
		width:35
	},{
		header:'型号',
		dataIndex:'prodCode',
		cls : 'x-grid-header-1',
		editor:{
			xtype:'textfield'
		},
		width:200
	},{
		header:'产品名称',
		dataIndex:'prodTitle',
		cls : 'x-grid-header-1',
		dbfind: "Product|pr_detail",
		style:'color:rgb(191, 60, 60)',
		editor: {
	        xtype: "multidbfindtrigger"
	    }, 
		width:300 
	},{
		header:'品牌',
		dataIndex:'brand',
		cls : 'x-grid-header-1',
		dbfind: "ProductBrand|pb_name",
		editor: {
	        xtype: "dbfindtrigger"
	    }, 
		width:100
	},{
		header:'单位',
		dataIndex:'unit',
		cls : 'x-grid-header-1',
		editor:{
			xtype:'combo',
			displayField:'display',
			valueField:'value',
			store: Ext.create('Ext.data.Store', {
				fields: ['display', 'value'],
				data:[
					{display:'PCS',value:'PCS'},
					{display:'KG',value:'KG'},
					{display:'M',value:'M'}]
			})
		},
		width:100
	},{
		header:'采购数量',
		dataIndex:'qty',
		xtype:'numbercolumn',
		style:'color:rgb(191, 60, 60)',
		align:"right",
		format:"0,000",
		cls : 'x-grid-header-1',
		editor:{
			xtype:'numberfield',
			hideTrigger:true,
			minValue:0
		},
		width:100
	}]
});