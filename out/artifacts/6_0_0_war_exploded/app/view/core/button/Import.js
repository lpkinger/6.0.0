Ext.define('erp.view.core.button.Import',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpImportButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpImportButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,  
       // disabled:true,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});