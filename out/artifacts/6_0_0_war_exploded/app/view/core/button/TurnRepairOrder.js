/**
 * 保存按钮
 * 适用于单据新增页面的保存，
 * 使用时，只需传递一个提交后台的saveUrl即可
 * @author yingp
 * @date 2012-08-03 10:45:49
 */	
Ext.define('erp.view.core.button.TurnRepairOrder',{
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnRepairOrderButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	tooltip: '生成派工单',
    	id: 'erpTurnRepairOrderButton',
        formBind: true,
    	text: $I18N.common.button.erpTurnRepairOrderButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120
	});