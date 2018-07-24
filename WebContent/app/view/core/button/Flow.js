/**
 * 查看审批流按钮
 */	
Ext.define('erp.view.core.button.Flow',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpFlowButton',
		iconCls: 'x-button-icon-scan',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpFlowButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
			var form = Ext.getCmp('form');
			form.FormUtil.onAdd(caller + '_flow', '已发起的流程', 'jsps/common/jprocessDeal.jsp?whoami=' + caller);		   
		}						
	});