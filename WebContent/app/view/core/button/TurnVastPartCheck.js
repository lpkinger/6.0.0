/**
 * 转分检按钮
 */	
Ext.define('erp.view.core.button.TurnVastPartCheck',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnVastPartCheckButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnVastPartCheckButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});