/**
 * 成本对账全部差异
 */
Ext.define('erp.view.co.cost.ShowDifferAll',{ 
	extend: 'Ext.Viewport', 
	alias: 'widget.showdifferall',
	layout: 'anchor', 
	hideBorders: true, 
	id: 'showdifferall', 
	initComponent : function(){
		Ext.data.Store.grouperIdFn = function(grouper) {
            return grouper.id || grouper.property;
        };
        Ext.data.Store.groupIdFn = function(group) {
            return group.key;
        };
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				id:'grid',
				anchor: '100% 100%',
				columnLines: true,
				columns: me.defaultColumns,
				store: me.store,
				dockedItems : [{
			    	xtype: 'toolbar',
			    	dock: 'top',
			    	margin:'0 0 5 0',
			    	style:{background:'#fff'},
			    	items: [{
						name: 'export',
						text: $I18N.common.button.erpExportButton,
						iconCls: 'x-button-icon-excel',
				    	cls: 'x-btn-gray'
					},'->',{
						text: $I18N.common.button.erpCloseButton,
						iconCls: 'x-button-icon-close',
						id:'close',
				    	cls: 'x-btn-gray',
					}]
			    }],
			    plugins : [ Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
				features : [ Ext.create('erp.view.core.feature.FloatingGrouping', {
					groupHeaderTpl : '{name} (共:{rows.length}条)'
				}) ]
			}]
		});
		me.callParent(arguments);
    },
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'tb_custcode',
        	type: 'string'
        },{
        	name: 'tb_custname',
        	type: 'string'
        },{
        	name: 'tb_currency',
        	type: 'string'
        },{
        	name: 'tb_kind',
        	type: 'string'
        },{
        	name: 'tb_code',
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
        },{
        	name: 'tb_catecode',
        	type:'string'
        }],
        data: [],
        groupers: [{
        	property: 'tb_custname',
        	transform: function(value) {
        		switch(value){
        		case '生产成本-直接材料':
        			return 1;
        		case '委托加工物资':
        			return 2;
        		}
        	}
        }],	
        groupField:'tb_custname'
    }),

    defaultColumns: [{
		dataIndex:'tb_catecode',
		text: '类型',
		hidden:true
	},{
		dataIndex: 'tb_custcode',
		cls: 'x-grid-header-1',
		text: '科目编号',
		width: 0
	},{
		dataIndex: 'tb_custname',
		cls: 'x-grid-header-1',
		text: '科目描述',
		width: 0
	},{
		dataIndex: 'tb_currency',
		cls: 'x-grid-header-1',
		text: '币别',
		width: 0
	},{
		dataIndex: 'tb_kind',
		cls: 'x-grid-header-1',
		text: '单据类型',
		width: 250,
		renderer: function(val, meta, record) {
			if (['期初余额', '期末余额', '本期借方发生', '本期贷方发生'].indexOf(record.get('tb_kind')) > -1) {
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
		format: '0,000.00',
		renderer: function(val, meta, record) {
			if(val != 0){
				return val;
			}
		}
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
		format: '0,000.00',
		renderer: function(val, meta, record) {
			if(val != 0){
				return val;
			}
		}
	},{
		dataIndex: 'tb_balance',
		cls: 'x-grid-header-1',
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