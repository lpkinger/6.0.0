Ext.define('erp.view.core.button.Load',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'load',
    	text: $I18N.common.button.erpLoadButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,  
       // disabled:true,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});