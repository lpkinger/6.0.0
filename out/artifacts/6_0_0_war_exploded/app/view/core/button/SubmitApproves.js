/**
 * 提交(批准)按钮
 */	
Ext.define('erp.view.core.button.SubmitApproves',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSubmitApprovesButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'submitApproves',
    	text: $I18N.common.button.erpSubmitApprovesButton,
    	//text:'提交(批准)',
    	
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});