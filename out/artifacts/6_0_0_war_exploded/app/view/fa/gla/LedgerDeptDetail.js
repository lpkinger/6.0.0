/**
 * 
 */
Ext.define('erp.view.fa.gla.LedgerDeptDetail',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.ledgerdeptdetail',
	layout : 'fit',
	id: 'ledgerdept', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'dp_month',
        	type: 'string'
        },{
        	name: 'dp_day',
        	type: 'string'
        },{
        	name: 'dp_vonumber',
        	type: 'string'
        },{
        	name: 'dp_catecode',
        	type: 'string'
        },{
        	name: 'dp_description',
        	type: 'string'
        },{
        	name: 'dp_explanation',
        	type: 'string'
        },{
        	name: 'dp_debit',
        	format: '0.000',
        	type: 'number'
        },{
        	name: 'dp_credit',
        	format: '0.000',
        	type: 'number'
        },{
        	name: 'dp_debitorcredit',
        	type: 'string'
        },{
        	name: 'dp_balance',
        	format: '0.000',
        	type: 'number'
        },{
        	name: 'dp_voucherid',
        	type: 'number'
        },{
        	name: 'deptcode',
        	type: 'string'
        },{
        	name: 'deptname',
        	type: 'string'
        },{
        	name: 'isCount',
        	type: 'bool'
        }],
        data: []
    }),
    defaultColumns: [{
    	dataIndex: 'dp_month',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '月',
    	width: 50
    },{
    	dataIndex: 'dp_day',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '日',
    	width: 50
    },{
    	dataIndex: 'dp_vonumber',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '凭证编号',
    	width: 120
    },{
    	dataIndex: 'dp_catecode',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目编号',
    	width: 120
    },{
    	dataIndex: 'dp_description',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目描述',
    	width: 200
    },{
    	dataIndex: 'deptcode',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '部门编号',
    	width: 100
    },{
    	dataIndex: 'deptname',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '部门名称',
    	width: 100
    },{
    	dataIndex: 'dp_explanation',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '摘要',
    	width: 300
    },{
		dataIndex: 'dp_debit',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '借方金额',
		width: 120,
		xtype: 'numbercolumn',
		format: '0,000.000',
		align: 'right'
	},{
		dataIndex: 'dp_credit',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '贷方金额',
		width: 120,
		xtype: 'numbercolumn',
		format: '0,000.000',
		align: 'right'
	},{
		dataIndex: 'dp_debitorcredit',
		sortable: false,
		cls: 'x-grid-header-1',
		text: '借贷<br>方向',
		width: 45
	},{
		dataIndex: 'dp_balance',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '余额',
		width: 120,
		xtype: 'numbercolumn',
		format: '0,000.000',
		align: 'right'
	}],
	
    bodyStyle:'background-color:#f1f1f1;',
    GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function(){
		this.columns = this.defaultColumns;
		this.callParent(arguments); 
	},
	viewConfig: { 
        getRowClass: function(record) { 
            return record.get('isCount') ? 'isCount' : null; 
        } 
    }
});