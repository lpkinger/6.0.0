/**
 * 
 */
Ext.define('erp.view.fa.gs.arQuery.ArDetailGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.ardetailgrid',
	layout : 'fit',
	id: 'ardetailgrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    dockedItems : [{
    	xtype: 'toolbar',
    	dock: 'top',
    	style:{background:'#fff'},
    	margin:'0 0 5 0',
    	items: [{
    		name: 'query',
    		text: $I18N.common.button.erpQueryButton,
    		iconCls: 'x-button-icon-query',
        	cls: 'x-btn-gray'
    	},{
			name: 'export',
			text: $I18N.common.button.erpExportButton,
			iconCls: 'x-button-icon-excel',
	    	cls: 'x-btn-gray',
	    	margin: '0 0 0 10'
		},{
			name: 'print',
			text: $I18N.common.button.erpPrintButton,
	    	iconCls: 'x-button-icon-print',
	    	margin: '0 0 0 10',
	    	cls: 'x-btn-gray'
		},'->',{
			text: $I18N.common.button.erpCloseButton,
			iconCls: 'x-button-icon-close',
			id:'close',
	    	cls: 'x-btn-gray',
	    	handler: function(){
	    		var main = parent.Ext.getCmp("content-panel"); 
	    		main.getActiveTab().close();
	    	}
		}]
    },{
    	xtype: 'toolbar',
    	dock: 'top',
    	items: [{
			xtype: 'displayfield',
	        fieldLabel: '账户编号',
	        value:'账户编号:&nbsp;&nbsp;&nbsp;&nbsp;'+accountcode,
	        hideLabel:true,
	        labelWidth:'100'
		},'','','','',{
			xtype: 'displayfield',
	        fieldLabel: '账户描述',
	        value:'账户描述:&nbsp;&nbsp;&nbsp;&nbsp;'+accountname,
	        hideLabel:true,
	        labelWidth:'100'
		},'','','','',{
			xtype: 'displayfield',
	        name: 'am_currency',
	        id: 'am_currency',
	        fieldLabel: '币别',
	        hideLabel:true,
	        value:'币别:&nbsp;&nbsp;&nbsp;&nbsp;'+currency,
	        labelWidth:'50'
		},'','','','',{
			xtype: 'displayfield',
	        name: 'am_yearmonth',
	        id: 'am_yearmonth',
	        labelAlign:'left',
	        hideLabel:true,
	        labelWidth:'50',
	        fieldLabel:'期间',
	        value:'期间:&nbsp;&nbsp;&nbsp;&nbsp;'+yearmonth
		}]
    	
    }],
  
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
        	name: 'ar_index',
        	type:'number'
        }],
        data: []
    }),
    defaultColumns: [{
		dataIndex: 'ar_date',
		cls: 'x-grid-header-1',
		text: '日期',
		width: 100
	},{
		dataIndex: 'ar_type',
		cls: 'x-grid-header-1',
		text: '类型',
		width: 110
	},{
		dataIndex: 'ar_code',
		cls: 'x-grid-header-1',
		text: '流水编号',
		width: 120
	},{
		dataIndex: 'ar_vouchercode',
		cls: 'x-grid-header-1',
		text: '凭证号',
		width: 120
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
			}else if(record.data['ar_index']==2){
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
			}else if(record.data['ar_index']==2){
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
			}else if(record.data['ar_index']==2){
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
    GridUtil: Ext.create('erp.util.GridUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
	initComponent : function(){
		this.columns = this.defaultColumns;
		this.callParent(arguments); 
	}
});