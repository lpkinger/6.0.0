/**
 * 确认出席人员按钮
 */	
Ext.define('erp.view.core.button.ConfirmMan',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmManButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: '确认出席人员',
    	id: 'erpConfirmManButton',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});