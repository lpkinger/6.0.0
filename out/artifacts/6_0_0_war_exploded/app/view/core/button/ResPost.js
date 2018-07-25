/**
 * 反过账按钮
 */	
Ext.define('erp.view.core.button.ResPost',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResPostButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpResPostButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});