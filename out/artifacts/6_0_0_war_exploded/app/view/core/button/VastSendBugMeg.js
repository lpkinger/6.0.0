/**
 * 批量发送BUG邮件
 */	
Ext.define('erp.view.core.button.VastSendBugMeg',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastSendBugMegButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpVastSendBugMegButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});