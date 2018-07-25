/**
 * 过账按钮
 */	
Ext.define('erp.view.core.button.Post',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPostButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPostButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});