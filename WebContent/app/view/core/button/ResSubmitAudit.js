/**
 * 反提交(审核)按钮
 */	
Ext.define('erp.view.core.button.ResSubmitAudit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResSubmitAuditButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'resSubmitAudit',
    	text: $I18N.common.button.erpResSubmitAuditButton,    	
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});