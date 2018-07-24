/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.GeneratePaCode',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGeneratePaCodeButton',
    	cls: 'x-btn-gray',
    	id: 'generatePaCodebtn',
    	text: $I18N.common.button.erpGeneratePaCodeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});