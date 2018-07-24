/**
 * 批量开票
 */	
Ext.define('erp.view.core.button.VastMakeBill',{
		extend: 'Ext.Button', 
		alias: 'widget.erpVastMakeBillButton',
		text: $I18N.common.button.erpVastMakeBillButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 60,
    	id: 'erpVastMakeBillButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});