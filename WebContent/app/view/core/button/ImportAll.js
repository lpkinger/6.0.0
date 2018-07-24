Ext.define('erp.view.core.button.ImportAll',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpImportAllButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpImportAllButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,  
       // disabled:true,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});