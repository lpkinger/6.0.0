/**
 * 生成备料按钮
 */	
Ext.define('erp.view.core.button.GetMaterial',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetMaterialButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id:'getMaterialbutton',
    	text: $I18N.common.button.erpGetMaterialButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});