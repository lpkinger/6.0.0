/**
 * 转回款通知单按钮
 */	
Ext.define('erp.view.core.button.TurnRecBalanceNotice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnRecBalanceNoticeButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnRecBalanceNoticeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});