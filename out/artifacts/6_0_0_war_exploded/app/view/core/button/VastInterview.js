/**
 * 批量删除按钮
 */	
Ext.define('erp.view.core.button.VastInterview',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastInterviewButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray-1',
    	id: 'VastInterview',
    	tooltip: '批量转面试',
    	id: 'erpVastInterviewButton',
    	text: $I18N.common.button.erpVastInterviewButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 100,
		handler: function(){
		}
	});