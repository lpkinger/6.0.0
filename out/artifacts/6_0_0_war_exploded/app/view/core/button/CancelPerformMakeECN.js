/**
 * 制造ECN批量取消执行
 */	
Ext.define('erp.view.core.button.CancelPerformMakeECN',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCancelPerformMakeECNButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCancelPerformMakeECN,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});