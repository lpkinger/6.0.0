/**
 * 
 */
Ext.define('erp.view.fa.gla.LedgerDetail',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.ledgerdetail',
	layout : 'fit',
	id: 'ledger', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    enableColumnHide: false,//是否允许隐藏列
    sortableColumns: false, //是否允许排序
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'ca_code',
        	type: 'string'
        },{
        	name: 'ca_name',
        	type: 'string'
        },{
        	name: 'sl_date',
        	type: 'string'
        },{
        	name: 'sl_vocode',
        	type: 'string'
        },{
        	name: 'sl_vonumber',
        	type: 'string'
        },{
        	name: 'sl_explanation',
        	type: 'string'
        },{
        	name: 'sl_othercate',
        	type: 'string'
        },{
        	name: 'sl_currency',
        	type: 'string'
        },{
        	name: 'sl_rate',
        	type: 'string'
        },{
        	name: 'sl_debit',
        	format: '0.00',
        	type: 'number'
        },{
        	name: 'sl_credit',
        	format: '0.00',
        	type: 'number'
        },{
        	name: 'sl_debitorcredit',
        	type: 'string'
        },{
        	name: 'sl_doubledebit',
        	format: '0.00',
        	type: 'number'
        },{
        	name: 'sl_doublecredit',
        	format: '0.00',
        	type: 'number'
        },{
        	name: 'sl_doublebalance',
        	format: '0.00',
        	type: 'number'
        },{
        	name: 'sl_balance',
        	format: '0.00',
        	type: 'number'
        },{
        	name: 'sl_voucherid',
        	type: 'number'
        },{
        	name: 'isCount',
        	type: 'string'
        },{
        	name: 'sl_detno',
        	type: 'number'
        }],
        data: [],
        onBeforeSort: function() {
        	if (this.sorters) {
        		this.sorters.each(function(sorter){
        			var fn = sorter.sort;
        			sorter.sort = function(m, n) {
        				// 合计行位置不变
                		var x = m.get('sl_detno'), y = n.get('sl_detno');
                		return x ? (y ? 0 : 1) : (y ? -1 : fn.call(sorter, m, n));
                	};
        		});
        	}
        }
    }),
    defaultColumns: [{
    	dataIndex: 'ca_code',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目编号',
    	width: 120,
    	hidden: true,
		renderer: function(val, meta, record) {
			if (record.get('isCount')!='begin') {
				return '';
			}
			return val;
		}
    },{
    	dataIndex: 'ca_name',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目名称',
    	width: 150,
    	hidden: true,
		renderer: function(val, meta, record) {
			if (record.get('isCount')!='begin') {
				return '';
			}
			return val;
		}
    },{
    	dataIndex: 'sl_date',
    	cls: 'x-grid-header-1',
    	width: 110,
    	text: '日期'
    },{
    	dataIndex: 'sl_vocode',
    	cls: 'x-grid-header-1',
    	text: '凭证流水',
    	width: 110
    },{
    	dataIndex: 'sl_vonumber',
    	cls: 'x-grid-header-1',
    	text: '凭证号',
    	align: 'right',
    	width: 60,
    	renderer: function(val, meta, record) {
			if(val != '0' && val != ''){
				return val;
			}
		}
    },{
    	dataIndex: 'sl_explanation',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '摘要',
    	width: 300
    },{
    	dataIndex: 'sl_othercate',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '对方科目',
    	width: 220,
    	hidden: true
    },{
		dataIndex: 'sl_debit',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '借方金额',
		width: 120,
		xtype: 'numbercolumn',
		format: '0,000.00',
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
		text: '贷方金额',
		width: 120,
		xtype: 'numbercolumn',
		format: '0,000.00',
		align: 'right',
		renderer: function(val, meta, record, x, y, store, view){
			if(val){
				return val.toFixed(2);
			}else{
				return '';
			}
		}
	},{
		dataIndex: 'sl_debitorcredit',
		sortable: false,
		cls: 'x-grid-header-1',
		text: '借贷<br>方向',
		width: 45
	},{
		dataIndex: 'sl_balance',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '余额',
		width: 120,
		xtype: 'numbercolumn',
		format: '0,000.00',
		align: 'right',
		renderer: function(val, meta, record, x, y, store, view){
			if(val){
				return val.toFixed(2);
			}else{
				return '';
			}
		}
	}],
	doubleColumns: [{
    	dataIndex: 'ca_code',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目编号',
    	width: 120,
    	hidden: true
    },{
    	dataIndex: 'ca_name',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目名称',
    	width: 250,
    	hidden: true
    },{
    	dataIndex: 'sl_date',
    	cls: 'x-grid-header-1',
    	width: 110,
    	text: '日期'
    },{
    	dataIndex: 'sl_vocode',
    	cls: 'x-grid-header-1',
    	text: '凭证流水',
    	width: 110
    },{
    	dataIndex: 'sl_vonumber',
    	cls: 'x-grid-header-1',
    	text: '凭证号',
    	width: 60,
    	renderer: function(val, meta, record) {
			if(val != '0' && val != ''){
				return val;
			}
		}
    },{
    	dataIndex: 'sl_explanation',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '摘要',
    	width: 300
    },{
    	dataIndex: 'sl_othercate',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '对方科目',
    	hidden: true,
    	width: 200
    },{
    	dataIndex: 'sl_currency',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '币别',
    	width: 45
    },{
    	dataIndex: 'sl_rate',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '汇率',
    	width: 60,
    	renderer: function(val, meta, record) {
			if(val != 0){
				return val;
			}
		}
    },{
		cls: 'x-grid-header-1',
		text: '借方金额',
		sortable: false,
		columns: [{
			dataIndex: 'sl_doubledebit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '原币',
			width: 120,
			xtype: 'numbercolumn',
			format: '0,000.00',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
		},{
			dataIndex: 'sl_debit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '本币',
			width: 120,
			xtype: 'numbercolumn',
			format: '0,000.00',
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
		text: '贷方金额',
		sortable: false,
		columns: [{
			dataIndex: 'sl_doublecredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '原币',
			width: 120,
			xtype: 'numbercolumn',
			format: '0,000.00',
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
			text: '本币',
			width: 120,
			xtype: 'numbercolumn',
			format: '0,000.00',
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
		text: '余额',
		sortable: false,
		align: 'right',
		columns: [{
			dataIndex: 'sl_debitorcredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '方向',
			width: 50
		},{
			dataIndex: 'sl_doublebalance',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '原币',
			width: 120,
			xtype: 'numbercolumn',
			format: '0,000.00',
			align: 'right',
			renderer: function(val, meta, record, x, y, store, view){
				if(val){
					return val.toFixed(2);
				}else{
					return '';
				}
			}
		},{
			dataIndex: 'sl_balance',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '本币',
			width: 120,
			xtype: 'numbercolumn',
			format: '0,000.00',
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
    GridUtil: Ext.create('erp.util.GridUtil'),
	listeners: {
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	initComponent : function(){
		this.columns = this.defaultColumns;
		this.callParent(arguments); 
	},
	plugins : [ Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
	viewConfig: { 
        getRowClass: function(record) { 
            return record.get('isCount') == 'count' ? 'isCount' : null; 
        } 
    }
});