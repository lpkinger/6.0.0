Ext.define('erp.view.core.button.RunLackMaterial',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRunLackMaterialButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpRunLackMaterialButton,
    	style: {
    		marginLeft: '5px'
        },
        width: 100,  
       // disabled:true,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});