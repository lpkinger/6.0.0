/**
 * 提交(审核)按钮
 */	
Ext.define('erp.view.core.button.SubmitAudit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSubmitAuditButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'submitAudit',
    	text: $I18N.common.button.erpSubmitAuditButton,
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});