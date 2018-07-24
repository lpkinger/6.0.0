/**
 * 批量转销售制造单按钮
 */	
Ext.define('erp.view.core.button.VastTurnSaleMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnSaleMakeButton',
		text: $I18N.common.button.erpVastTurnSaleMakeButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 120,
    	id: 'erpVastTurnSaleMakeButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});