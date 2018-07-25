/**
 * 工序转移
 */
Ext.define('erp.view.core.button.ProcessTransfer',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProcessTransferButton',
		param: [],
		id: 'erpProcessTransferButton',
		text: '工序转移',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler : function(){}
	});