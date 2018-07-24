/**
 * 转业务招待费报销单按钮
 */	
Ext.define('erp.view.core.button.TurnYWZDBX',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnYWZDBXButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnYWZDBXButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 140,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});