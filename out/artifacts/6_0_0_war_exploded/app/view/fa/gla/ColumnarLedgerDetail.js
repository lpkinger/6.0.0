/**
 * 
 */
Ext.define('erp.view.fa.gla.ColumnarLedgerDetail',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.columnarledgerdetail',
	layout : 'fit',
	id: 'columnarledgerdetail', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    enableColumnHide: false,//是否允许隐藏列
    sortableColumns: false, //是否允许排序
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'sl_date',
        	type: 'string'
        },{
        	name: 'sl_vonumber',
        	type: 'string'
        },{
        	name: 'sl_explanation',
        	type: 'string'
        },{
        	name: 'sl_yearmonth',
        	type: 'string'
        },{
        	name: 'sl_currency',
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
        	name: 'sl_doubledebit',
        	format: '0.00',
        	type: 'number'
        },{
        	name: 'sl_doublecredit',
        	format: '0.00',
        	type: 'number'
        },{
        	name: 'sl_debitorcredit',
        	type: 'string'
        },{
        	name: 'sl_balance',
        	format: '0.00',
        	type: 'number'
        },{
        	name: 'sl_voucherid',
        	type: 'number'
        },{
        	name: 'sl_vocode',
        	type: 'string'
        },{
        	name: 'isCount',
        	type: 'bool'
        }],
        data: [],
        onBeforeSort: function() {
        	if (this.sorters) {
        		this.sorters.each(function(sorter){
        			var fn = sorter.sort;
        			sorter.sort = function(m, n) {
        				// 合计行位置不变
                		var x = m.get('isCount'), y = n.get('isCount');
                		return x ? (y ? 0 : 1) : (y ? -1 : fn.call(sorter, m, n));
                	};
        		});
        	}
        }
    }),
    defaultColumns: [{
    	dataIndex: 'sl_date',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	width: 110,
    	text: '日期'
    },{
    	dataIndex: 'sl_vonumber',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '凭证号',
    	xtype: 'numbercolumn',
    	align: 'right',
    	width: 60
    },{
    	dataIndex: 'sl_explanation',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '摘要',
    	width: 300
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
	},{
		cls: 'x-grid-header-1',
		text: '余额',
		sortable: false,
		columns: [{
			text: '方向',
			cls: 'x-grid-header-1',
			dataIndex: 'sl_debitorcredit',
			sortable: false,
			width: 50
		},{
			dataIndex: 'sl_balance',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '金额',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	}],
	doubleColumns: [{
    	dataIndex: 'sl_date',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	width: 110,
    	text: '日期'
    },{
    	dataIndex: 'sl_vonumber',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '凭证号',
    	xtype: 'numbercolumn',
    	align: 'right',
    	width: 60
    },{
    	dataIndex: 'sl_explanation',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '摘要',
    	width: 300
    },{
    	dataIndex: 'sl_currency',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '币别',
    	width: 50
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
	},{
		cls: 'x-grid-header-1',
		text: '余额',
		sortable: false,
		columns: [{
			text: '方向',
			cls: 'x-grid-header-1',
			dataIndex: 'sl_debitorcredit',
			sortable: false,
			width: 50
		},{
			dataIndex: 'sl_balance',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '金额',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
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
	viewConfig: { 
		 getRowClass: function(record) { 
			 console.log(record.get('sl_explanation'));
			 if(['期末余额', '本期合计', '本年累计'].indexOf(record.get('sl_explanation')) > -1){
				 return 'isCount';
			 }
	     } 
    }
});