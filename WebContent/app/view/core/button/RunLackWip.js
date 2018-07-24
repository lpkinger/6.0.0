Ext.define('erp.view.core.button.RunLackWip',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRunLackWipButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpRunLackWipButton,
    	style: {
    		marginLeft: '5px'
        },
        width: 130,  
       // disabled:true,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});