Ext.define('erp.view.core.button.Checktask',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpChecktaskButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'Checktask',
    	text: '更改处理状态',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});