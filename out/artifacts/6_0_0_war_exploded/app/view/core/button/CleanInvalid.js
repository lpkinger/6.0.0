/**
 * 清除无效数据
 */	
Ext.define('erp.view.core.button.CleanInvalid',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCleanInvalidButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'CleanInvalid',
    	text: $I18N.common.button.erpCleanInvalidButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});