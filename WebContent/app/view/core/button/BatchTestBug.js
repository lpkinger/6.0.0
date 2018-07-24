/**
 * 批量测试BUG
 */	
Ext.define('erp.view.core.button.BatchTestBug',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchTestBugButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBatchTestBugButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});