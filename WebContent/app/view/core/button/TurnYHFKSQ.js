/**
 * 转银行付款申请单按钮
 */	
Ext.define('erp.view.core.button.TurnYHFKSQ',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnYHFKSQButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnYHFKSQButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});