/**
 * 转通知单按钮
 */	
Ext.define('erp.view.core.button.TurnNotify',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnNotifyButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnNotifyButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});