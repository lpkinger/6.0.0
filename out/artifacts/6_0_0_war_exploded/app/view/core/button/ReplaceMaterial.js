/**
 * 替代按钮
 */	
Ext.define('erp.view.core.button.ReplaceMaterial',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReplaceMaterialButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id:'replaceMaterialbutton',
    	text: $I18N.common.button.erpReplaceMaterialButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});