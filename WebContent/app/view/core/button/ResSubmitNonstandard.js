Ext.define('erp.view.core.button.ResSubmitNoStandard',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResSubmitNoStandardButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: '反提交(转非标准)',
    	style: {
    		marginLeft: '10px'
        },
        width:120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});