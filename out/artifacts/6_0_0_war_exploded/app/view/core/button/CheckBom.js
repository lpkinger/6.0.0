/**
 * BOM有效性检查按钮
 */	
Ext.define('erp.view.core.button.CheckBom',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCheckBomButton',
        cls: 'x-btn-blue',
        formBind: true,//form.isValid() == false时,按钮disabled
    	id: 'checkbombtn',
    	text: '检查',
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});