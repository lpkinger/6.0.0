Ext.define('erp.view.fa.ars.VoucherNumber', { 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				anchor: '100% 13%',
				xtype: 'form',
				bodyStyle: 'background:#f1f1f1;',
				layout: 'column',
				defaults: {
					margin: '5 10 0 10',
					labelWidth: 80
				},
				items: [{
					xtype: 'monthdatefield',
					id: 'vo_yearmonth',
					name: 'vo_yearmonth',
					fieldLabel: '总账期间',
					fieldStyle: 'background:#fff;color:#515151;',
					columnWidth: .25
				},{
					xtype: 'displayfield',
					id: 'vo_breaks',
					columnWidth: .75,
					fieldLabel: '断号'
				}],
				tbar: {margin:'0 0 5 0',style:{background:'#fff'},items:[{
					name: 'query',
					id: 'query',
					text: $I18N.common.button.erpQueryButton,
					iconCls: 'x-button-icon-query',
			    	cls: 'x-btn-gray'
				}, {
					margin:'0 0 0 5',
					id: 'number',
					iconCls: 'x-button-icon-add',
			    	cls: 'x-btn-gray',
			    	text: $I18N.common.button.erpVoucherNumberButton
				},{
					margin:'0 0 0 5',
					id: 'save',
					iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	text: $I18N.common.button.erpSaveButton
				},'->',{
			    	text: $I18N.common.button.erpCloseButton,
			    	iconCls: 'x-button-icon-close',
			        cls: 'x-btn-gray',
			        handler: function(){
			        	var main = parent.Ext.getCmp("content-panel"); 
			        	main.getActiveTab().close();
			        }
				}]}
			},{
				anchor: '100% 87%',
				xtype: 'erpEditorColumnGridPanel',
				caller: 'VoucherNumber',
				selModel: Ext.create('Ext.selection.RowModel'),
				condition: 'vo_yearmonth=' + Ext.Date.format(new Date(), 'Ym') + 
					' order by vo_lead,vo_number',
				autoRowNumber: true
			}] 
		}); 
		me.callParent(arguments); 
	} 
});