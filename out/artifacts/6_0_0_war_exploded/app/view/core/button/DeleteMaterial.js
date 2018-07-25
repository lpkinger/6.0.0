/**
 * 删除按钮
 */	
Ext.define('erp.view.core.button.DeleteMaterial',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeleteMaterialButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id:'deleteMaterialbutton',
    	text: $I18N.common.button.erpDeleteMaterialButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});