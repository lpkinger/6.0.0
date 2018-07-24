/**
 * 
 */
Ext.define('erp.view.fa.arp.QueryGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.querygrid',
	layout : 'fit',
	id: 'querygrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'vm_yearmonth',
        	type: 'string'
        },{
        	name: 'vm_vendcode',
        	type: 'string'
        },{
        	name: 'vm_vendname',
        	type: 'string'
        },{
        	name: 'vm_currency',
        	type: 'string'
        },{
        	name: 'asl_date',
        	type: 'string'
        },{
        	name: 'asl_source',
        	type: 'string'
        },{
        	name: 'asl_othercode',
        	type: 'string'
        },{
        	name: 'asl_action',
        	type: 'string'
        },{
        	name: 'asl_explanation',
        	type: 'string'
        },{
        	name: 'asl_apamount',
        	type: 'number'
        },{
        	name: 'asl_payamount',
        	type: 'number'
        },{
        	name: 'asl_balance',
        	type: 'number'
        },{
        	name: 'index',
        	type: 'number'
        },{
        	name: 'vm_esamount',
        	type: 'number'
        }],
        data: []
    }),
    defaultColumns: [{
    	dataIndex: 'vm_yearmonth',
    	cls: 'x-grid-header-1',
    	width: 100,
    	text: '期间'
    },{
    	dataIndex: 'vm_vendcode',
    	cls: 'x-grid-header-1',
    	text: '供应商号',
    	width: 120
    },{
    	dataIndex: 'vm_vendname',
    	cls: 'x-grid-header-1',
    	text: '供应商名',
    	width: 200
    },{
    	dataIndex: 'vm_currency',
    	cls: 'x-grid-header-1',
    	text: '币别',
    	width: 70
    },{
		dataIndex: 'asl_date',
		cls: 'x-grid-header-1',
		text: '日期',
		width: 100
	},{
		dataIndex: 'asl_source',
		cls: 'x-grid-header-1',
		text: '单据类型',
		width: 100
	},{
		dataIndex: 'asl_othercode',
		cls: 'x-grid-header-1',
		text: '单据编号',
		width: 150
	},{
		dataIndex: 'asl_action',
		cls: 'x-grid-header-1',
		text: '操作',
		width: 100
	},{
		dataIndex: 'asl_explanation',
		cls: 'x-grid-header-1',
		text: '说明',
		width: 150
	},{
		dataIndex: 'asl_apamount',
		cls: 'x-grid-header-1',
		text: '应付金额',
		xtype: 'numbercolumn',
		format: '0,000.00',
		align: 'right',
		width: 100
	},{
		dataIndex: 'asl_payamount',
		cls: 'x-grid-header-1',
		text: '付款金额',
		xtype: 'numbercolumn',
		format: '0,000.00',
		align: 'right',
		width: 100
	},{
		dataIndex: 'vm_esamount',
		cls: 'x-grid-header-1',
		text: '暂估金额',
		xtype: 'numbercolumn',
		format: '0,000.00',
		align: 'right',
		width: 100
	},{
		dataIndex: 'asl_balance',
		cls: 'x-grid-header-1',
		text: '余额',
		xtype: 'numbercolumn',
		format: '0,000.00',
		align: 'right',
		width: 100
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
            return record.get('index')%2 == 0 ? (!Ext.isEmpty(record.get('cm_id')) ? 'custom-first' : 'custom') : 
            	(!Ext.isEmpty(record.get('cm_id')) ? 'custom-alt-first' : 'custom-alt');
        } 
    }
});