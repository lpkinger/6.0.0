/**
 * 批量变更申请
 */	
Ext.define('erp.view.core.button.ReplaceChange',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReplaceChangeButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpReplaceChangeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});