/**
 * 转客户按钮
 */	
Ext.define('erp.view.core.button.TurnVastOaacceptance',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnVastOaacceptanceButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnVastOaacceptanceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});