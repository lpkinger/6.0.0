Ext.define('erp.view.hr.wage.conf.PersonTaxGridPanel', {
	extend:'Ext.grid.Panel',
	alias:'widget.erpPersonTaxGridPanel',
	requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	region:'south',
	layout:'fit',
	id:'persontaxgrid',
	emptyText:$I18N.common.grid.emptyText,
	columnLines:true,
	autoScroll:true,
	bodyStyle: 'background-color:#f1f1f1;',
	bbar: {xtype: 'erpToolbar'},
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	store: Ext.create('Ext.data.Store', {
			    fields:[
			    'WP_ID',
			    'WP_STARTAMOUNT',
			    'WP_ENDAMOUNT',
			    'WP_TAXRATE',
			    'WP_QUICKDEDUCTION']
	}),
    columns: [{
    	header:'ID',
    	text:'ID',
    	dataIndex:'WP_ID',
    	cls:'x-grid-header-1',
    	align:'right',
    	xtype:'numbercolumn',
    	width:0
    },{
    	header:'应税所得开始值',
    	text:'应税所得开始值',
    	dataIndex:'WP_STARTAMOUNT',
    	name:'WP_STARTAMOUNT',
    	cls:'x-grid-header-1',
    	editable:true,
    	align:'left',
		field:{
			xtype:'numberfield',
			allowBlank:false
		},
    	width:150
    },{
    	header:'应税所得结束值',
    	text:'应税所得结束值',
    	dataIndex:'WP_ENDAMOUNT',
    	name:'WP_ENDAMOUNT',
    	cls:'x-grid-header-1',
    	editable:true,
    	align:'left',
		field:{
			xtype:'numberfield',
			allowBlank:false
		},
    	width:150
    },{
    	header:'税率',
    	text:'税率',
    	dataIndex:'WP_TAXRATE',
    	name:'WP_TAXRATE',
    	cls:'x-grid-header-1',
    	editable:true,
    	align:'left',
		field:{
			xtype:'numberfield',
			allowBlank:false
		},
		renderer: function(value){
	        return value*100 + '%';
	    },
		
		
    	width:150
    },{
    	header:'速算扣除数',
    	text:'速算扣除数',
    	dataIndex:'WP_QUICKDEDUCTION',
    	name:'WP_QUICKDEDUCTION',
    	cls:'x-grid-header-1',
    	editable:true,
    	align:'left',
		field:{
			xtype:'numberfield',
			allowBlank:false
		},
    	width:150
    }],
	initComponent : function(){
		this.callParent(arguments);
	}
});