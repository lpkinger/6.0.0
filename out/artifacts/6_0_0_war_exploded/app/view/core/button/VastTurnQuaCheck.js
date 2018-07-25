/**
 * 批量转质量检验单按钮
 */	
Ext.define('erp.view.core.button.VastTurnQuaCheck',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnQuaCheckButton',
		text: $I18N.common.button.erpVastTurnQuaCheckButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpVastTurnQuaCheckButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});