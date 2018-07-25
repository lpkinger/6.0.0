/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.AutoArrange',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAutoArrangeButton',
		iconCls: 'x-button-icon-code',
    	cls: 'x-btn-gray',
    	text: '自动编排',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	style: {
    		marginLeft: '10px'
        },
        width: 85,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});