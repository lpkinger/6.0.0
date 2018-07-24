/**
 * 
 */
Ext.define('erp.view.fa.gla.VoucherSumDetail',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.vouchersumdetail',
	layout : 'fit',
	id: 'vouchersumdetail', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'ca_code',
        	type: 'string'
        },{
        	name: 'ca_name',
        	type: 'string'
        },{
        	name: 'sl_currency',
        	type: 'string'
        },{
        	name: 'sl_debit',
        	type: 'number'
        },{
        	name: 'sl_credit',
        	type: 'number'
        },{
        	name: 'sl_doubledebit',
        	type: 'number'
        },{
        	name: 'sl_doublecredit',
        	type: 'number'
        },{
        	name: 'sl_index',
        	type: 'number'
        }],
        data: []
    }),
    defaultColumns: [{
    	dataIndex: 'ca_code',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	width: 130,
    	text: '科目编号'
    },{
    	dataIndex: 'ca_name',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目名称',
    	width: 350
    },{
		dataIndex: 'sl_debit',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '借方',
		width: 120,
		xtype: 'numbercolumn',
		align: 'right'
	},{
		dataIndex: 'sl_credit',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '贷方',
		width: 120,
		xtype: 'numbercolumn',
		align: 'right'
	}],
	doubleColumns: [{
    	dataIndex: 'ca_code',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	width: 120,
    	text: '科目编号'
    },{
    	dataIndex: 'ca_name',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目名称',
    	width: 350
    },{
    	dataIndex: 'sl_currency',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '币别',
    	width: 70
    },{
		cls: 'x-grid-header-1',
		text: '原币',
		sortable: false,
		width: 240,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			sortable: false,
			dataIndex: 'sl_doubledebit',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'sl_doublecredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '本币',
		sortable: false,
		width: 240,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			sortable: false,
			dataIndex: 'sl_debit',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'sl_credit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	}],
    bodyStyle:'background-color:#f1f1f1;',
    cls: 'custom-grid',
    GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function(){
		this.columns = this.defaultColumns;
		this.callParent(arguments);
	},
	viewConfig: { 
        getRowClass: function(record) { 
            return '合计' == record.get('ca_name') ? 'isCount' : null; 
        } 
    }
});