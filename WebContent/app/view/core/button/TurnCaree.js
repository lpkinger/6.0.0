/**
 * 保存按钮
 * 适用于单据新增页面的保存，
 * 使用时，只需传递一个提交后台的saveUrl即可
 * @author yingp
 * @date 2012-08-03 10:45:49
 */	
Ext.define('erp.view.core.button.TurnCaree',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnCareeButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'TurnCaree',
    	tooltip: '批量转录用申请单',
    	id: 'erpTurnCareeButton',
    	text: $I18N.common.button.erpTurnCareeButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		handler: function(){
		}
	});