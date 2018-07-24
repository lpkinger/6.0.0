/**
 * 转费用报销单按钮
 */	
Ext.define('erp.view.core.button.TurnFYBX',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnFYBXButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnFYBXButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});