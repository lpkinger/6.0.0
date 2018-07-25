/**
 * 设为主料
 */	
Ext.define('erp.view.core.button.SetMain',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSetMainButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'SetMain',
    	text: "设为主料",
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});