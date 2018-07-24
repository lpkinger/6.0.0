Ext.define('erp.view.core.button.TDMaterialSearch',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTDMaterialSearch',
		id:'erpTDMaterialSearch',
    	cls: 'x-btn-gray',	    
    	text: '替代料查询',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});