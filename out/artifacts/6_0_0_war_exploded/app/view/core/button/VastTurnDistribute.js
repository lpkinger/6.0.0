/**
 * 批量转分配单按钮
 */	
Ext.define('erp.view.core.button.VastTurnDistribute',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnDistributeButton',
		text: $I18N.common.button.erpVastTurnDistributeButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpVastTurnDistributeButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});