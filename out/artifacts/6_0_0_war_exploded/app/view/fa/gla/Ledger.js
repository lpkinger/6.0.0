/**
 * 
 */
Ext.define('erp.view.fa.gla.Ledger',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.ledger',
	layout : 'fit',
	id: 'ledger', 
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
        	name: 'am_asscode',
        	type: 'string'
        },{
        	name: 'am_assname',
        	type: 'string'
        },{
        	name: 'am_asscode',
        	type: 'string'
        },{
        	name: 'am_asstype',
        	type: 'string'
        },{
        	name: 'cm_yearmonth',
        	type: 'string'
        },{
        	name: 'vd_explanation',
        	type: 'string'
        },{
        	name: 'cm_debit',
        	type: 'number'
        },{
        	name: 'cm_credit',
        	type: 'number'
        },{
        	name: 'cmc_debit',
        	type: 'number'
        },{
        	name: 'cmc_credit',
        	type: 'number'
        },{
        	name: 'cmc_doubledebit',
        	type: 'number'
        },{
        	name: 'cmc_doublecredit',
        	type: 'number'
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
        	name: 'index',
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
    	width: 200
    },{
    	dataIndex: 'am_asscode',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	hidden: true,
    	text: '核算编号',
    	width: 90
    },{
    	dataIndex: 'am_assname',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	hidden: true,
    	text: '核算名称',
    	width: 150
    },{
    	dataIndex: 'cm_yearmonth',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '期间',
    	width: 70
    },{
		dataIndex: 'vd_explanation',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '摘要',
		width: 70
	},{
		dataIndex: 'cm_debit',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '借方',
		width: 120,
		xtype: 'numbercolumn',
		align: 'right',
		renderer: function(val, meta, record, x, y, store, view){
			if(val){
				return val.toFixed(2);
			}else{
				return '';
			}
		}
	},{
		dataIndex: 'cm_credit',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '贷方',
		width: 120,
		xtype: 'numbercolumn',
		align: 'right',
		renderer: function(val, meta, record, x, y, store, view){
			if(val){
				return val.toFixed(2);
			}else{
				return '';
			}
		}
	},{
		cls: 'x-grid-header-1',
		text: '余额',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'sl_debit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
		},{
			dataIndex: 'sl_credit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
		}]
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
    	width: 180
    },{
    	dataIndex: 'am_asscode',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	hidden: true,
    	text: '核算编号',
    	width: 80
    },{
    	dataIndex: 'am_assname',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	hidden: true,
    	text: '核算名称',
    	width: 120
    },{
    	dataIndex: 'cm_yearmonth',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '期间',
    	width: 70
    },{
    	dataIndex: 'cmc_currency',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '币别',
		width: 70
    },{
		dataIndex: 'vd_explanation',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '摘要',
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
			dataIndex: 'cmc_doubledebit',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
		},{
			dataIndex: 'cmc_doublecredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
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
			dataIndex: 'cmc_debit',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
		},{
			dataIndex: 'cmc_credit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
		}]
	},{
		cls: 'x-grid-header-1',
		text: '原币余额',
		sortable: false,
		width: 240,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			sortable: false,
			dataIndex: 'sl_doubledebit',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
		},{
			dataIndex: 'sl_doublecredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
		}]
	},{
		cls: 'x-grid-header-1',
		text: '本币余额',
		sortable: false,
		width: 240,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			sortable: false,
			dataIndex: 'sl_debit',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
		},{
			dataIndex: 'sl_credit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
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
            return record.get('index')%2 == 1 ? (!Ext.isEmpty(record.get('ca_code')) ? 'custom-first' : 'custom') : 
            	(!Ext.isEmpty(record.get('ca_code')) ? 'custom-alt-first' : 'custom-alt');
        } 
    }
});