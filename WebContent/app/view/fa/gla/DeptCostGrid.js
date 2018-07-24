/**
 * 
 */
Ext.define('erp.view.fa.gla.DeptCostGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.deptcostgrid',
	layout : 'fit',
	id: 'deptcost', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'am_yearmonth',
        	type: 'string'
        },{
        	name: 'ca_code',
        	type: 'string'
        },{
        	name: 'ca_name',
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
    	dataIndex: 'ca_code',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目编号',
    	width: 120
    },{
    	dataIndex: 'ca_name',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目名称',
    	width: 150
    }],

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