Ext.define('erp.view.core.button.Endfeedback',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpEndfeedbackButton',
		text: "结束问题",
		iconCls: 'x-button-icon-close',
		id:'erpEndfeedbackButton',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});