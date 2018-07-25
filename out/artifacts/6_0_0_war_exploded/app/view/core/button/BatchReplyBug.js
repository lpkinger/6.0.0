/**
 * 批量回复BUG
 */	
Ext.define('erp.view.core.button.BatchReplyBug',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchReplyBugButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBatchReplyBugButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});