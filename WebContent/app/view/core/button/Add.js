/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.Add',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAddButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	id: 'addbtn',
    	text: $I18N.common.button.erpAddButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});