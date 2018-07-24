Ext.define('erp.view.core.button.InsertEmployee',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpInsertEmployeeButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpInsertEmployeeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,  
       // disabled:true,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});