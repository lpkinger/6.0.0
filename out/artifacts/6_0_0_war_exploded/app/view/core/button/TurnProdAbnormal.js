/**
 * 转物料品质异常联络单
 */	
Ext.define('erp.view.core.button.TurnProdAbnormal',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProdAbnormalButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'erpTurnProdAbnormalButton',
    	text: $I18N.common.button.erpTurnProdAbnormalButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 180
	});