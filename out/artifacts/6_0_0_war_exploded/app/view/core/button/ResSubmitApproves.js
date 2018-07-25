/**
 * 反提交按钮
 */	
Ext.define('erp.view.core.button.ResSubmitApproves',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResSubmitApprovesButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'resSubmitApproves',
    	text: $I18N.common.button.erpResSubmitApprovesButton,
    	//text:'反提交(批准)',
    	
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});