/**
 * 转ECN按钮
 */	
Ext.define('erp.view.core.button.TurnMakeExp',{
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnMakeExpButton',
		iconCls: 'x-button-icon-ecn',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnMakeExpButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 70,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});