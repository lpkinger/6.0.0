Ext.define('erp.view.core.button.InMaterialNeed',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpInMaterialNeedButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'load',
    	text: $I18N.common.button.erpInMaterialNeedButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,  
       // disabled:true,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});