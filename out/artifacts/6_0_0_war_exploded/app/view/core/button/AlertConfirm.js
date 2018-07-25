Ext.define('erp.view.core.button.AlertConfirm',{ 
		extend: 'Ext.Button', 
		FormUtil: Ext.create('erp.util.FormUtil'),
		alias: 'widget.erpAlertConfirmButton',
		param: [],
		id: 'erpAlertConfirmButton',
		text: '确认',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
});