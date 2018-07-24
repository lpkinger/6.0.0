/**
 * 转模具模具委托保管书按钮
 */	
Ext.define('erp.view.core.button.TurnMJProject',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnMJProjectButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnMJProjectButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 160,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});