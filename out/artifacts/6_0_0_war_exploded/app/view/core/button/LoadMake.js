Ext.define('erp.view.core.button.LoadMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadMakeButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpLoadMakeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,  
       // disabled:true,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});