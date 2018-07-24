/**
 * 取消凭证按钮
 */	
Ext.define('erp.view.core.button.VoCancel',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVoUnCreateButton',
		iconCls: 'x-button-icon-add',
		cls: 'x-btn-gray',
    	id: 'vocancel',
    	text: $I18N.common.button.erpVoUnCreateButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 85,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});