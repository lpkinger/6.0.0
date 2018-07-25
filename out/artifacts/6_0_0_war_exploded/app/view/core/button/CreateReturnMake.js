/**
 * 生成退料单
 */	
Ext.define('erp.view.core.button.CreateReturnMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCreateReturnMakeButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id:'erpCreateReturnMakeButton',
    	text: $I18N.common.button.erpCreateReturnMakeButton,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});