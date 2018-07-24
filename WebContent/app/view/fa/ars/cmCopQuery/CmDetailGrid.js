/**
 * 
 */
Ext.define('erp.view.fa.ars.cmCopQuery.CmDetailGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.cmcopdetailgrid',
	layout : 'fit',
	id: 'cmcopdetailgrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    dockedItems : [{
    	xtype: 'toolbar',
    	dock: 'top',
    	items: [{
    		name: 'query',
    		text: $I18N.common.button.erpQueryButton,
    		iconCls: 'x-button-icon-query',
        	cls: 'x-btn-gray'
    	},/*'<div style="font-size:1">显示销售发票明细</div>',*/'->',{
			name: 'export',
			text: $I18N.common.button.erpExportButton,
			iconCls: 'x-button-icon-excel',
	    	cls: 'x-btn-gray',
	    	margin: '0 4 0 0'
		},'-',{
			name: 'print',
			text: $I18N.common.button.erpPrintButton,
	    	iconCls: 'x-button-icon-print',
	    	margin: '0 4 0 0',
	    	cls: 'x-btn-gray'
		},'-',{
			text: $I18N.common.button.erpCloseButton,
			iconCls: 'x-button-icon-close',
			id:'close',
	    	cls: 'x-btn-gray',
	    	margin: '0 4 0 0',
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
	        fieldLabel: '客户名称',
	        value:'客户名称:&nbsp;&nbsp;&nbsp;&nbsp;'+custname,
	        hideLabel:true,
	        labelWidth:'100'
		},'',{
			xtype: 'displayfield',
	        fieldLabel: '公司名',
	        value:'公司名:&nbsp;&nbsp;&nbsp;&nbsp;'+cop,
	        hideLabel:true,
	        labelWidth:'100'
		},'',{
			xtype: 'displayfield',
	        name: 'cm_currency',
	        id: 'cm_currency',
	        fieldLabel: '币别',
	        hideLabel:true,
	        value:'币别:&nbsp;&nbsp;&nbsp;&nbsp;'+currency,
	        labelWidth:'50'
		},'','','','',{
			xtype: 'displayfield',
	        name: 'cm_yearmonth',
	        id: 'cm_yearmonth',
	        labelAlign:'left',
	        hideLabel:true,
	        labelWidth:'50',
	        fieldLabel:'期间',
	        value:'期间:&nbsp;&nbsp;&nbsp;&nbsp;'+yearmonth
		}]
    	
    }],
  
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'tb_code',
        	type: 'string'
        },{
        	name: 'tb_kind',
        	type: 'string'
        },{
        	name: 'tb_remark',
        	type: 'string'
        },{
        	name: 'tb_vouc',
        	type: 'string'
        },{
        	name: 'tb_date',
        	type: 'string'
        },{
        	name: 'tb_aramount',
        	type: 'string'
        },{
        	name: 'tb_inoutno',
        	type: 'string'
        },{
        	name: 'tb_pdno',
        	type: 'string'
        },{
        	name: 'tb_ordercode',
        	type: 'string'
        },{
        	name: 'tb_prodcode',
        	type: 'string'
        },{
        	name: 'tb_qty',
        	type: 'string'
        },{
        	name: 'tb_price',
        	type: 'string'
        },{
        	name: 'tb_rbamount',
        	type: 'string'
        },{
        	name: 'tb_rbamounts',
        	type: 'string'
        },{
        	name: 'tb_aramounts',
        	type: 'string'
        },{
        	name: 'tb_balance',
        	type: 'string'
        },{
        	name: 'tb_index',
        	type: 'number'
        },{
        	name: 'tb_id',
        	type:'number'
        }],
        data: []
    }),
/*
 * 备注：
*tb_index 
*value = 1		应收明细 ，发出商品 第一行期初余额详情  
*value = 2		应收明细，发出商品 中间的信息详情
*value = 3		应收明细，发出商品 最后一行期余额详情
*value = 4		第二 三张表的表头
*value = 5		空白行
*value = 6		发货单明细
*
*/
    defaultColumns: [{
		dataIndex: 'tb_date',
		cls: 'x-grid-header-1',
		text: '日期',
		width: 100,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_kind',
		cls: 'x-grid-header-1',
		text: '单据类型',
		width: 120,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_code',
		cls: 'x-grid-header-1',
		text: '单据编号',
		width: 150,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_remark',
		cls: 'x-grid-header-1',
		text: '描述',
		width: 200,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_vouc',
		cls: 'x-grid-header-1',
		text: '凭证号',
		width: 150,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_aramount',
		cls: 'x-grid-header-1',
		text: '应收金额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==1||record.data['tb_index']==3||record.data['tb_index']==6){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['tb_index']==2){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'tb_rbamount',
		cls: 'x-grid-header-1',
		text: '收款金额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center;">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==1||record.data['tb_index']==3||record.data['tb_index']==6){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['tb_index']==2){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'tb_aramounts',
		cls: 'x-grid-header-1',
		text: '(销售)应收金额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==1||record.data['tb_index']==3||record.data['tb_index']==6){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['tb_index']==2){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'tb_rbamounts',
		cls: 'x-grid-header-1',
		text: '(销售)收款金额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center;">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==1||record.data['tb_index']==3||record.data['tb_index']==6){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['tb_index']==2){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'tb_balance',
		cls: 'x-grid-header-1',
		text: '余额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==6){
				return '';
			}else{
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	}],
	detailColumns: [{
		dataIndex: 'tb_date',
		cls: 'x-grid-header-1',
		text: '日期',
		width: 100,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_kind',
		cls: 'x-grid-header-1',
		text: '单据类型',
		width: 120,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_code',
		cls: 'x-grid-header-1',
		text: '单据编号',
		width: 150,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_remark',
		cls: 'x-grid-header-1',
		text: '描述',
		width: 100,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_vouc',
		cls: 'x-grid-header-1',
		text: '凭证号',
		width: 150,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_inoutno',
		cls: 'x-grid-header-1',
		text: '出入库单号',
		width: 130,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_pdno',
		cls: 'x-grid-header-1',
		text: '出入库序号',
		width: 100,
		align: 'center',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				if (val == 0)
					return '';
				return val;
			}
		}
	},{
		dataIndex: 'tb_ordercode',
		cls: 'x-grid-header-1',
		text: '订单号',
		width: 150,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_prodcode',
		cls: 'x-grid-header-1',
		text: '物料编号',
		width: 150,
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else{
				return val;
			}
		}
	},{
		dataIndex: 'tb_qty',
		cls: 'x-grid-header-1',
		text: '数量',
		width: 100,
		align:'right',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==1||record.data['tb_index']==3){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0');
					return val;
				}
			}else if(record.data['tb_index']==2||record.data['tb_index']==6){
				val = Ext.util.Format.number(val,'0');
				return val;
			}
		}
	},{
		dataIndex: 'tb_price',
		cls: 'x-grid-header-1',
		text: '单价',
		width: 100,
		align:'right',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==1||record.data['tb_index']==3){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['tb_index']==2||record.data['tb_index']==6){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'tb_aramount',
		cls: 'x-grid-header-1',
		text: '应收金额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record, x, y, s){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==1||record.data['tb_index']==3||record.data['tb_index']==6){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['tb_index']==2){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'tb_rbamount',
		cls: 'x-grid-header-1',
		text: '收款金额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record, x, y, s){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center;">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==1||record.data['tb_index']==3||record.data['tb_index']==6){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['tb_index']==2){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'tb_aramounts',
		cls: 'x-grid-header-1',
		text: '(销售)应收金额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==1||record.data['tb_index']==3||record.data['tb_index']==6){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['tb_index']==2){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'tb_rbamounts',
		cls: 'x-grid-header-1',
		text: '(销售)收款金额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center;">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==1||record.data['tb_index']==3||record.data['tb_index']==6){
				if(val==0||val=='0'){
					return '';
				}else{
					val = Ext.util.Format.number(val,'0,000.00');
					return val;
				}
			}else if(record.data['tb_index']==2){
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	},{
		dataIndex: 'tb_balance',
		cls: 'x-grid-header-1',
		text: '余额',
		width: 120,
		align:'right',
		xtype:'numbercolumn',
		renderer:function(val, meta, record){
			if(record.data['tb_index']==4||record.data['tb_index']==5){
				val = '<div style="text-align:center">'+val+'</div>';
				return val;
			}else if(record.data['tb_index']==6){
				return '';
			}else{
				val = Ext.util.Format.number(val,'0,000.00');
				return val;
			}
		}
	}],
//    bodyStyle:'background-color:#f1f1f1;',
    GridUtil: Ext.create('erp.util.GridUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
	initComponent : function(){
		this.columns = this.defaultColumns;
		this.callParent(arguments); 
	},

	viewConfig: { 
        getRowClass: function(record) { 
//            return record.get('isCount') ? 'isCount' : null; 
        	if(record.get('tb_index')=='4'){
        		return 'custom-total';
        	}
        } 
    }
});