Ext.define('erp.view.core.button.WipNeed',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpWipNeedButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray', 
    	text: $I18N.common.button.erpWipNeedButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 160,  
       // disabled:true,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});