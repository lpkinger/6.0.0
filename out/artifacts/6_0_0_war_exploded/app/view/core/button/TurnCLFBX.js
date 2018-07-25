/**
 * 转差旅费报销单按钮
 */	
Ext.define('erp.view.core.button.TurnCLFBX',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnCLFBXButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnCLFBXButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});