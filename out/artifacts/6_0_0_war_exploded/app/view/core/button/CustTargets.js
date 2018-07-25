/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.CustTargets',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCustTargetsButton',
		iconCls: 'x-button-icon-code',
    	cls: 'x-btn-gray',
    	id: 'custtargetsbtn',
    	text: '信用指标得分',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});