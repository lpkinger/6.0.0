/**
 * MRP关闭
 */	
Ext.define('erp.view.core.button.MrpClose',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMrpCloseButton',
		//iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'MrpClose',
    	text: $I18N.common.button.erpMrpCloseButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 70,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});