/**
 * 批量转人才库按钮
 */	
Ext.define('erp.view.core.button.VastJointalcpool',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastJointalcpoolButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray-1',
    	id: 'VastJointalcpool',
    	tooltip: '批量转人才库',
    	id: 'erpVastJointalcpoolButton',
    	text: $I18N.common.button.erpVastJointalcpoolButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		handler: function(){
		}
	});