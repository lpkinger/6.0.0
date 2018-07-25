/**
 * 
 */
Ext.define('erp.view.fa.gla.EmplCostGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.emplcostgrid',
	layout : 'fit',
	id: 'emplcost', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'am_yearmonth',
        	type: 'string'
        },{
        	name: 'em_name',
        	type: 'string'
        },{
        	name: 'em_depart',
        	type: 'string'
        }],
        data: []
    }),
    columns: [{
    	dataIndex: 'am_yearmonth',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '期间',
    	width: 90
    },{
    	dataIndex: 'em_name',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '人员名称',
    	width: 100
    },{
    	dataIndex: 'em_depart',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '所属部门',
    	width: 150
    }/*,{
		dataIndex: 'sl_balance',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '余额',
		width: 120,
		xtype: 'numbercolumn',
		format: '0,000.000',
		align: 'right'
	}*/],

    bodyStyle:'background-color:#f1f1f1;',
    GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function(){
		this.callParent(arguments); 
	},
	viewConfig: { 
        getRowClass: function(record) { 
            return record.get('isCount') ? 'isCount' : null; 
        } 
    }
});