/**
 * 生成工艺按钮
 */	
Ext.define('erp.view.core.button.GetCraft',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetCraftButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id:'getCraftbutton',
    	text: $I18N.common.button.erpGetCraftButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});