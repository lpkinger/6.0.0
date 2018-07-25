Ext.define('erp.view.co.cost.ShowDiffer',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'form',
				anchor: '100% 13%',
				bodyStyle: 'background:#f1f1f1',
				layout: 'column',
				items: [{
					xtype: 'displayfield',
					fieldLabel: '科目',
					margin: '5 0 0 0',
					labelAlign : 'right',
					id:'name',
					value: name
				}],
				buttonAlign: 'center',
				buttons: [{
					name: 'export',
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray',
			    	margin: '0 4 0 0'
				},{
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
				xtype: 'grid',
				id:'grid',
				anchor: '100% 87%',
				columnLines: true,
				columns: me.defaultColumns,
				plugins : [ Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
				store: me.store
			}]
		});
		me.callParent(arguments);
	},
	store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'tb_code',
        	type: 'string'
        },{
        	name: 'tb_kind',
        	type: 'string'
        },{
        	name: 'tb_vouchercode',
        	type: 'string'
        },{
        	name: 'tb_vonumber',
        	type: 'string'
        },{
        	name: 'tb_aramount',
        	type: 'string'
        },{
        	name: 'tb_debitorcredit',
        	type: 'string'
        },{
        	name: 'tb_glamount',
        	type: 'string'
        },{
        	name: 'tb_balance',
        	type: 'string'
        },{
        	name: 'tb_index',
        	type: 'number'
        },{
        	name: 'tb_void',
        	type:'number'
        }],
        data: []
    }),
    defaultColumns: [{
		dataIndex: 'tb_kind',
		cls: 'x-grid-header-1',
		text: '单据类型',
		width: 250,
		renderer: function(val, meta, record) {
			if (['期初余额', '期末余额', '本期借方发生', '本期贷方发生'].indexOf(val) > -1) {
				meta.style = 'font-weight: 700';
			}
			return val;
		}
	},{
		dataIndex: 'tb_aramount',
		cls: 'x-grid-header-1',
		text: '成本系统金额',
		width: 130,
		align:'right', 
		xtype:'numbercolumn',
		format: '0,000.00'
	},{
		dataIndex: 'tb_vonumber',
		xtype: 'linkcolumn',
		cls: 'x-grid-header-1',
		text: '凭证号',
		width: 80,
		align: 'center',
		linkUrl: 'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS{tb_void}&gridCondition=vd_voidIS{tb_void}',
		linkTabTitle: '凭证({tb_vouchercode})'
	},{
		dataIndex: 'tb_debitorcredit',
		cls: 'x-grid-header-1',
		text: '方向',
		width: 0
	},{
		dataIndex: 'tb_glamount',
		cls: 'x-grid-header-1',
		text: '总账系统金额',
		width: 130,
		align:'right',
		xtype:'numbercolumn',
		format: '0,000.00'
	},{
		dataIndex: 'tb_balance',
		cls: 'x-grid-header-1 isCount',
		text: '差额',
		width: 130,
		align:'right',
		format: '0,000.00',
		xtype:'numbercolumn',
		renderer: function(val, meta, record) {
			if(val != 0){
				if (['期初余额', '期末余额', '本期借方发生', '本期贷方发生'].indexOf(record.get('tb_kind')) > -1) {
					meta.style = 'font-weight: 700';
				}
				return val;
			}
		}
	}],
	
    GridUtil: Ext.create('erp.util.GridUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil')
});