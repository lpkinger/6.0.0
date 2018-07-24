/**
 * 变更明细按钮
 */	
Ext.define('erp.view.core.button.ChangeMaterial',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpChangeMaterialButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id:'changeMaterialbutton',
    	text: $I18N.common.button.erpChangeMaterialButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});