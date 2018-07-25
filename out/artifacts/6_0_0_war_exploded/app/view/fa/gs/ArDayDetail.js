/**
 * 
 */
Ext.define('erp.view.fa.gs.ArDayDetail',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.ardaydetail',
	layout : 'fit',
	id: 'ardaydetail', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    enableColumnHide: false,//是否允许隐藏列
    sortableColumns: false, //是否允许排序
    store: Ext.create('Ext.data.Store', {
    	fields:[{
        	name: 'ar_code',
        	type: 'string'
        },{
        	name: 'ar_type',
        	type: 'string'
        },{
        	name: 'ar_date',
        	type: 'string'
        },{
        	name: 'ar_custcode',
        	type: 'string'
        },{
        	name: 'ar_custname',
        	type: 'string'
        },{
        	name: 'ar_vendcode',
        	type: 'string'
        },{
        	name: 'ar_vendname',
        	type: 'string'
        },{
        	name: 'ar_payment',
        	type: 'string'
        },{
        	name: 'ar_deposit',
        	type: 'string'
        },{
        	name: 'ar_balance',
        	type: 'string'
        },{
        	name: 'ar_recordman',
        	type: 'string'
        },{
        	name: 'ar_sourcetype',
        	type: 'string'
        },{
        	name: 'ar_source',
        	type: 'string'
        },{
        	name: 'ar_vouchercode',
        	type:'string'
        },{
        	name: 'ar_memo',
        	type:'string'
        },{
        	name: 'ar_id',
        	type:'number'
        },{
        	name: 'ar_table',
        	type:'string'
        },{
        	name: 'ar_accountname',
        	type:'string'
        },{
        	name: 'ar_accountcurrency',
        	type:'string'
        },{
        	name: 'ar_index',
        	type:'number'
        }],
        data: [],
        onBeforeSort: function() {
        	if (this.sorters) {
        		this.sorters.each(function(sorter){
        			var fn = sorter.sort;
        			sorter.sort = function(m, n) {
        				// 合计行位置不变
                		var x = m.get('ar_index'), y = n.get('ar_index');
                		return x ? (y ? 0 : 1) : (y ? -1 : fn.call(sorter, m, n));
                	};
        		});
        	}
        }
    }),
    defaultColumns: [{
		dataIndex: 'ar_accountname',
		cls: 'x-grid-header-1',
		text: '账号名称',
		width: 150
	},{
		dataIndex: 'ar_accountcurrency',
		cls: 'x-grid-header-1',
		text: '币别',
		width: 65
	},{
		dataIndex: 'ar_date',
		cls: 'x-grid-header-1',
		text: '日期',
		width: 100
	},{
		dataIndex: 'ar_type',
		cls: 'x-grid-header-1',
		text: '类型',
		width: 100
	},{
		dataIndex: 'ar_code',
		cls: 'x-grid-header-1',
		text: '流水编号',
		width: 110
	},{
		dataIndex: 'ar_vouchercode',
		cls: 'x-grid-header-1',
		text: '凭证号',
		width: 110
	},{
		dataIndex: 'ar_memo',
		cls: 'x-grid-header-1',
		text: '备注',
		width: 120
	},{
		dataIndex: 'ar_deposit',
		cls: 'x-grid-header-1',
		text: '收入金额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['ar_index']==1||record.data['ar_index']==3){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['ar_index']==2||record.data['ar_index']==2.1){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'ar_payment',
		cls: 'x-grid-header-1',
		text: '支出金额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['ar_index']==1||record.data['ar_index']==3){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['ar_index']==2||record.data['ar_index']==2.1){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'ar_balance',
		cls: 'x-grid-header-1',
		text: '余额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['ar_index']==1||record.data['ar_index']==3){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['ar_index']==2||record.data['ar_index']==2.1){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'ar_custcode',
		cls: 'x-grid-header-1',
		text: '客户编号',
		width: 100
	},{
		dataIndex: 'ar_custname',
		cls: 'x-grid-header-1',
		text: '客户名称',
		width: 200
	},{
		dataIndex: 'ar_vendcode',
		cls: 'x-grid-header-1',
		text: '供应商编号',
		width: 100
	},{
		dataIndex: 'ar_vendname',
		cls: 'x-grid-header-1',
		text: '供应商名称',
		width: 200
	},{
		dataIndex: 'ar_recordman',
		cls: 'x-grid-header-1',
		text: '录入人',
		width: 80
	},{
		dataIndex: 'ar_sourcetype',
		cls: 'x-grid-header-1',
		text: '来源类型',
		width: 100
	},{
		dataIndex: 'ar_source',
		cls: 'x-grid-header-1',
		text: '来源单号',
		width: 110
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
            return record.get('ar_index') == '1' || record.get('ar_index') == '3' ? 'isCount' : null; 
        } 
    }
});