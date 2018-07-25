/**
 * 
 */
Ext.define('erp.view.fa.ars.QueryGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.querygrid',
	layout : 'fit',
	id: 'querygrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'cm_yearmonth',
        	type: 'string'
        },{
        	name: 'cm_custcode',
        	type: 'string'
        },{
        	name: 'cm_custname',
        	type: 'string'
        },{
        	name: 'cm_currency',
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
        	name: 'asl_aramount',
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
        }],
        data: []
    }),
    defaultColumns: [{
    	dataIndex: 'cm_yearmonth',
    	cls: 'x-grid-header-1',
    	width: 100,
    	text: '期间'
    },{
    	dataIndex: 'cm_custcode',
    	cls: 'x-grid-header-1',
    	text: '客户编码',
    	width: 120
    },{
    	dataIndex: 'cm_custname',
    	cls: 'x-grid-header-1',
    	text: '客户名称',
    	width: 200
    },{
    	dataIndex: 'cm_currency',
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
		dataIndex: 'asl_aramount',
		cls: 'x-grid-header-1',
		text: '应收金额',
		width: 100
	},{
		dataIndex: 'asl_payamount',
		cls: 'x-grid-header-1',
		text: '收款金额',
		width: 100
	},{
		dataIndex: 'asl_balance',
		cls: 'x-grid-header-1',
		text: '余额',
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