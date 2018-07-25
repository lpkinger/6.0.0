/**
 * 用料表计算按钮
 */	
Ext.define('erp.view.core.button.CalMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCalMakeButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'calMakebtn',
    	text: $I18N.common.button.erpCalMakeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});