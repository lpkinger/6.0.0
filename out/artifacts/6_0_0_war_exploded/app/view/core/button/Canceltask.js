Ext.define('erp.view.core.button.Canceltask',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCanceltaskButton',
		text: "取消处理",
		iconCls: 'x-button-icon-close',
		id:'erpCanceltaskButton',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});